package com.dmlab.cita.server.service;

import com.citahub.cita.abi.EventEncoder;
import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.Address;
import com.citahub.cita.abi.datatypes.Event;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.abi.datatypes.Utf8String;
import com.citahub.cita.abi.datatypes.generated.Uint64;
import com.citahub.cita.crypto.Credentials;
import com.citahub.cita.crypto.sm2.SM2;
import com.citahub.cita.crypto.sm2.SM2KeyPair;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.RemoteCall;
import com.citahub.cita.protocol.core.Request;
import com.citahub.cita.protocol.core.methods.request.AppFilter;
import com.citahub.cita.protocol.core.methods.response.*;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tuples.generated.Tuple2;
import com.citahub.cita.tx.Contract;
import com.citahub.cita.tx.RawTransactionManager;
import com.citahub.cita.tx.TransactionManager;
import com.citahub.cita.utils.HexUtil;
import com.dmlab.cita.server.config.CitaConfig;
import com.dmlab.cita.server.contracts.Broker;
import com.dmlab.cita.server.contracts.DataSwap;
import com.dmlab.cita.server.utils.CITAUtils;
import com.dmlab.cita.server.utils.FunctionUtils;
import com.dmlab.cita.server.utils.IBTPUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.moandjiezana.toml.Toml;
import io.grpc.stub.StreamObserver;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pb.AppchainPluginGrpc.AppchainPluginImplBase;
import pb.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@GrpcService
@Component
@Slf4j
public class AppchainPluginServiceImpl extends AppchainPluginImplBase {

    private static String TYPE = "cita";
    private CITAj client;
    private CitaConfig config;
    private String pierId;
    private int version;
    private BigInteger chainId;

    private static BlockingQueue<IBTP> eventC = new ArrayBlockingQueue<IBTP>(1024);
    private Broker broker;
    private DataSwap dataSwap;


    @Override
    public void initialize(InitializeRequest request, StreamObserver<Empty> responseObserver) {
        String configPath = request.getConfigPath();
        pierId = request.getPierId();
        try {
            Toml toml = new Toml().read(Files.newInputStream(Path.of(configPath, "cita.toml")));
            String addr = toml.getString("addr", "https://testnet.citahub.com");
            String name = toml.getString("name", "cita");
            String contractAddress = toml.getString("contract_address");
            String dataSwapAddress = toml.getString("data_swap_address");
            String algo = toml.getString("algo");
            String key = toml.getString("key");
            config = new CitaConfig(addr, name, contractAddress, dataSwapAddress, algo, key);
        } catch (IOException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        client = CITAj.build(new HttpService(config.getAddr()));
        try {
            version = CITAUtils.getVersion(client);
            chainId = CITAUtils.getChainId(client);
            log.info("Cita version:{}, chainId:{}", version, chainId.intValue());
        } catch (IOException e) {
            log.error("", e);
            responseObserver.onError(e);
        }
        TransactionManager citaTxManager = null;
        if ("sm2".equalsIgnoreCase(config.getAlgo())) {
            citaTxManager = RawTransactionManager.createSM2Manager(client, new SM2().fromPrivateKey(config.getPrivateKey()), 5, 3000);
        } else {
            citaTxManager = new RawTransactionManager(
                    client, Credentials.create(config.getPrivateKey()), 5, 3000);
        }


        broker = Broker.load(config.getContractAddress(), client, citaTxManager);
        dataSwap = DataSwap.load(config.getDataSwapAddress(), client, citaTxManager);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void start(Empty request, StreamObserver<Empty> responseObserver) {
        Runnable runnable = () -> {
            final Event event = new Event("throwEvent",
                    Arrays.<TypeReference<?>>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {
                    }, new TypeReference<Address>() {
                    }, new TypeReference<Address>() {
                    }, new TypeReference<Utf8String>() {
                    }, new TypeReference<Utf8String>() {
                    }, new TypeReference<Utf8String>() {
                    }, new TypeReference<Utf8String>() {
                    }, new TypeReference<Utf8String>() {
                    }));
            String topic = EventEncoder.encode(event);
            BigInteger startBlock = getCurBlockNumber();

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                BigInteger endBlock = getCurBlockNumber();

                if (endBlock.compareTo(startBlock) < 0) {
                    log.warn("cur block is smaller than start block, start block: {}, end block: {}", startBlock.toString(), endBlock.toString());
                    continue;
                }

                log.info("start block: {}, end block: {}", startBlock.toString(), endBlock.toString());
                AppFilter filter = new AppFilter(
                        DefaultBlockParameter.valueOf(startBlock),
                        DefaultBlockParameter.valueOf(endBlock),
                        broker.getContractAddress());
                filter.addSingleTopic(topic);

                try {
                    AppLog appLog = client.appGetLogs(filter).send();

                    for (AppLog.LogResult logResult : appLog.getLogs()) {
                        if (logResult instanceof AppLog.LogObject) {
                            Log eventLog = ((AppLog.LogObject) logResult).get();
                            EventValues eventValues = Contract.staticExtractEventParameters(event, eventLog);
                            Broker.ThrowEventEventResponse typedResponse = new Broker.ThrowEventEventResponse();
                            typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                            typedResponse.fid = (String) eventValues.getNonIndexedValues().get(2).getValue();
                            typedResponse.tid = (String) eventValues.getNonIndexedValues().get(3).getValue();
                            typedResponse.funcs = (String) eventValues.getNonIndexedValues().get(4).getValue();
                            typedResponse.args = (String) eventValues.getNonIndexedValues().get(5).getValue();
                            typedResponse.argscb = (String) eventValues.getNonIndexedValues().get(6).getValue();
                            typedResponse.argsrb = (String) eventValues.getNonIndexedValues().get(7).getValue();

                            log.info("accept event: {} ", typedResponse);
                            try {
                                eventC.put(IBTPUtils.convertFromEvent(typedResponse, pierId));
                            } catch (Exception e) {
                                log.error("", e);
                            }

                            startBlock = endBlock.add(BigInteger.ONE);
                        }
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private BigInteger getCurBlockNumber() {
        while (true) {
            try {
                return client.appBlockNumber().send().getBlockNumber();
            } catch (IOException e) {
                log.error("", e);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stop(Empty request, StreamObserver<Empty> responseObserver) {
        super.stop(request, responseObserver);
    }


    @Override
    public void getIBTP(Empty request, StreamObserver<IBTP> responseObserver) {
        while (true) {
            try {
                IBTP ibtp = eventC.take();
                responseObserver.onNext(ibtp);
            } catch (InterruptedException e) {
                log.error("", e);
                responseObserver.onError(e);
                break;
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public void submitIBTP(IBTP request, StreamObserver<SubmitIBTPResponse> responseObserver) {
        SubmitIBTPResponse sir = SubmitIBTPResponse.getDefaultInstance();
        payload payload = null;
        content content = null;
        try {
            payload = pb.payload.parseFrom(request.getPayload());
            content = pb.content.parseFrom(payload.getContent());
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }

        log.info("ibtp: {}", request.toString());
        log.info("content: {}", content.toString());

        if (IBTPUtils.category(request) == IBTP.Category.UNKNOWN) {
            responseObserver.onError(new IllegalArgumentException("invalid ibtp category"));
            return;
        }

        if (IBTPUtils.category(request) == IBTP.Category.RESPONSE && content.getFunc().trim().equals("")) {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = invokeInterchainWithError(request.getFrom(), request.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            } catch (Exception e) {
                log.error("", e);
                responseObserver.onError(e);
                return;
            }
            boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
            if (status) {
                responseObserver.onError(new IllegalStateException("update index for ibtp failed" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
            sir = sir.toBuilder().setStatus(true).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }


        Function func = null;
        try {
            func = FunctionUtils.packFunc(content.getFunc(), content.getArgsList());
        } catch (Exception e) {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = invokeInterchainWithError(request.getFrom(), request.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            } catch (Exception t) {
                log.error("", e);
                responseObserver.onError(t);
                log.error("invokeInterchainWithError Exception: {}", t.toString());
                return;
            }
            boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
            log.info("invokeInterchainWithError status: {}", status);
            if (status) {
                responseObserver.onError(new IllegalStateException("update index for ibtp failed" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
            sir = sir.toBuilder().setStatus(true).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }
        String funcEncoder = FunctionEncoder.encode(func);

        log.info("func encode:{}" + funcEncoder);
        TransactionReceipt receipt = null;
        try {
            receipt = invokeInterchain(request.getFrom(), request.getIndex(), content.getDstContractId(), IBTPUtils.category(request), HexUtil.hexToBytes(funcEncoder.substring(2)));
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            log.error("invokeInterchain Exception: {}", e.toString());
            return;
        }

        boolean callBackStatus = false;
        byte[][] result = new byte[0][];
        boolean status = StringUtils.hasLength(receipt.getErrorMessage());
        log.info("invokeInterchain status: {}", status);
        if (!status) {
            Log log = receipt.getLogs().get(receipt.getLogs().size() - 1);
            try {
                result = FunctionUtils.unPackFunc(content.getFunc(), log.getData());
                callBackStatus = true;
            } catch (Exception e) {
                e.printStackTrace();
                responseObserver.onError(e);
                return;
            }
        } else {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = invokeInterchainWithError(request.getFrom(), request.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            } catch (Exception e) {
                log.error("", e);
                responseObserver.onError(e);
                return;
            }
            status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
            if (status) {
                responseObserver.onError(new IllegalStateException("invalid index for ibtp:" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
            sir = sir.toBuilder().setStatus(false).setMessage("InvokeInterchain tx execution failed").build();
        }


        if (IBTPUtils.category(request) == IBTP.Category.RESPONSE) {
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }

        IBTP callBack = null;
        try {
            callBack = generateCallBack(request, result, callBackStatus);
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        sir = sir.toBuilder().setResult(callBack).setStatus(callBackStatus).build();
        responseObserver.onNext(sir);
        responseObserver.onCompleted();
    }


    @Override
    public void rollbackIBTP(pb.RollbackIBTPRequest request, StreamObserver<pb.RollbackIBTPResponse> responseObserver) {
        RollbackIBTPResponse rollbackIBTPResponse = RollbackIBTPResponse.getDefaultInstance();
        payload payload = null;
        content content = null;
        try {
            payload = pb.payload.parseFrom(request.getIbtp().getPayload());
            content = pb.content.parseFrom(payload.getContent());
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }

        if (!content.getFunc().equals("interchainCharge")) {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
            return;
        }

        TransactionReceipt transactionReceipt = null;
        try {
            Function func = FunctionUtils.packFunc(content.getRollback(), content.getArgsRbList());
            String encode = FunctionEncoder.encode(func);
            transactionReceipt = invokeInterchain(request.getIbtp().getTo(), request.getIbtp().getIndex(), content.getSrcContractId(), IBTP.Category.RESPONSE, HexUtil.hexToBytes(encode.substring(2)));
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            return;

        }
        boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
        if (status) {
            rollbackIBTPResponse = rollbackIBTPResponse.toBuilder().setStatus(false).setMessage("wrong contract doesn't emit log event").build();
        }
        responseObserver.onNext(rollbackIBTPResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void increaseInMeta(pb.IBTP request, StreamObserver<pb.IBTP> responseObserver) {
        IBTP ibtp = null;
        try {
            ibtp = generateCallBack(request, null, false);
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        try {
            TransactionReceipt transactionReceipt = invokeInterchainWithError(ibtp.getFrom(), ibtp.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
            if (status) {
                responseObserver.onError(new IllegalStateException("invalid index for ibtp:" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        responseObserver.onNext(ibtp);
        responseObserver.onCompleted();
    }

    @Override
    public void getOutMessage(GetOutMessageRequest request, StreamObserver<IBTP> responseObserver) {
        BigInteger block = null;
        try {
            block = broker.getOutMessage(request.getTo(), BigInteger.valueOf(request.getIdx())).send();
            log.info("get missing ibtp to:{}, idx:{}, height:{}", request.getTo(), request.getIdx(), block.intValue());
            Flowable<Broker.ThrowEventEventResponse> flowable = broker.throwEventEventFlowable(DefaultBlockParameter.valueOf(block), DefaultBlockParameter.valueOf(block));
            Broker.ThrowEventEventResponse throwEventEventResponse = flowable.blockingFirst();
            log.info("throwEventEventResponse to:{}, idx:{}", throwEventEventResponse.to, throwEventEventResponse.index.intValue());

            IBTP ibtp = null;
            if (throwEventEventResponse.to.equalsIgnoreCase(request.getTo()) && throwEventEventResponse.index.longValue() == request.getIdx()) {
                try {
                    ibtp = IBTPUtils.convertFromEvent(throwEventEventResponse, pierId);
                    responseObserver.onNext(ibtp);
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    log.error("", e);
                    responseObserver.onError(e);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getInMessage(GetInMessageRequest request, StreamObserver<GetInMessageResponse> responseObserver) {
        BigInteger inBlock = null;
        try {
            inBlock = broker.getInMessage(request.getFrom(), BigInteger.valueOf(request.getIdx())).send();
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(GetInMessageResponse.newBuilder().addResult(ByteString.copyFrom(inBlock.toByteArray())).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getInMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        Future<Tuple2<List<String>, List<BigInteger>>> tuple2Future = broker.getInnerMeta().sendAsync();
        getMeta(responseObserver, tuple2Future);
    }

    @Override
    public void getOutMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        Future<Tuple2<List<String>, List<BigInteger>>> tuple2Future = broker.getOuterMeta().sendAsync();
        getMeta(responseObserver, tuple2Future);
    }

    @Override
    public void getCallbackMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        Future<Tuple2<List<String>, List<BigInteger>>> tuple2Future = broker.getCallbackMeta().sendAsync();
        getMeta(responseObserver, tuple2Future);
    }

    private void getMeta(StreamObserver<GetMetaResponse> responseObserver, Future<Tuple2<List<String>, List<BigInteger>>> tuple2Future) {
        Tuple2<List<String>, List<BigInteger>> listListTuple2 = null;
        try {
            listListTuple2 = tuple2Future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        List<String> appchainIds = listListTuple2.getValue1();
        List<BigInteger> indics = listListTuple2.getValue2();
        Map<String, Long> meta = new HashMap<>();
        for (int i = 0; i < appchainIds.size() && appchainIds.size() == indics.size(); i++) {
            meta.put(appchainIds.get(i), indics.get(i).longValue());
        }
        GetMetaResponse getMetaResponse = GetMetaResponse.newBuilder().putAllMeta(meta).build();
        responseObserver.onNext(getMetaResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void commitCallback(IBTP request, StreamObserver<Empty> responseObserver) {
        super.commitCallback(request, responseObserver);
    }

    @Override
    public void getReceipt(IBTP request, StreamObserver<IBTP> responseObserver) {
        payload payload = null;
        content content = null;
        try {
            payload = pb.payload.parseFrom(request.getPayload());
            content = pb.content.parseFrom(payload.getContent());
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }

        Future<BigInteger> bigIntegerFuture = broker.getInMessage(request.getFrom(), BigInteger.valueOf(request.getIndex())).sendAsync();
        BigInteger blockNum = null;
        try {
            blockNum = bigIntegerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        boolean callBackStatus = false;
        byte[][] result = new byte[0][];
        Future<AppBlock> appBlockFuture = client.appGetBlockByNumber(DefaultBlockParameter.valueOf(blockNum), true).sendAsync();
        try {
            pb.content finalContent = content;
            for (AppBlock.TransactionObject tx : appBlockFuture.get().getBlock().getBody().getTransactions()) {
                Future<AppGetTransactionReceipt> appGetTransactionReceiptFuture = client.appGetTransactionReceipt(tx.getHash()).sendAsync();
                TransactionReceipt transactionReceipt = null;
                try {
                    transactionReceipt = appGetTransactionReceiptFuture.get().getTransactionReceipt();
                } catch (Exception e) {
                    responseObserver.onError(e);
                    return;
                }
                if (config.getContractAddress().equalsIgnoreCase(transactionReceipt.getTo())) {
                    if (transactionReceipt.getLogs().size() == 1) {
                        Log log = transactionReceipt.getLogs().get(transactionReceipt.getLogs().size() - 1);
                        try {
                            result = FunctionUtils.unPackFunc(finalContent.getFunc(), log.getData());
                            callBackStatus = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            responseObserver.onError(e);
                            return;
                        }
                    }

                }
            }
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
        }
        IBTP callBack = null;
        try {
            callBack = generateCallBack(request, result, callBackStatus);
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }
        responseObserver.onNext(callBack);
        responseObserver.onCompleted();
    }


    @Override
    public void checkHash(CheckHashRequest request, StreamObserver<CheckHashResponse> responseObserver) {
        long currentHeight;
        AppBlock.Header header;
        byte[] headerData;
        byte[] receiptData;
        try {
            currentHeight = client.appBlockNumber()
                    .send().getBlockNumber().longValue();

            long validUntilBlock = currentHeight + 80;
            String nonce = CITAUtils.getNonce();
            RemoteCall<TransactionReceipt> receiptRemoteCall = dataSwap.getData(request.getHash(), BigInteger.ZERO, 10000000L, nonce, validUntilBlock, version, chainId, "");
            Future<TransactionReceipt> transactionReceiptFuture = receiptRemoteCall.sendAsync();
            TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
            log.info("tx hash: {}", transactionReceipt.getTransactionHash());
            List<DataSwap.LogDataExistsEventResponse> logs = dataSwap.getLogDataExistsEvents(transactionReceipt);
            Request<?, AppBlock> appBlockRequest = client.appGetBlockByNumber(DefaultBlockParameter.valueOf(transactionReceipt.getBlockNumber()), false);


            header = appBlockRequest.send().getBlock().getHeader();
            ObjectMapper mapper = new ObjectMapper();
            headerData = mapper.writeValueAsBytes(header);
            receiptData = mapper.writeValueAsBytes(transactionReceipt);
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
            return;
        }

        responseObserver.onNext(CheckHashResponse.newBuilder()
                .setHeaderData(ByteString.copyFrom(headerData))
                .setReceiptData(ByteString.copyFrom(receiptData))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void name(Empty request, StreamObserver<NameResponse> responseObserver) {
        responseObserver.onNext(NameResponse.newBuilder().setName(config.getName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void type(Empty request, StreamObserver<TypeResponse> responseObserver) {
        responseObserver.onNext(TypeResponse.newBuilder().setType(TYPE).build());
        responseObserver.onCompleted();
    }


    public TransactionReceipt invokeInterchain(
            String srcChainMethod, Long index, String destAddr, IBTP.Category category, byte[] bizCallData) throws Exception {
        long currentHeight = client.appBlockNumber()
                .send().getBlockNumber().longValue();
        long validUntilBlock = currentHeight + 80;
        String nonce = CITAUtils.getNonce();
        RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = broker.invokeInterchain(srcChainMethod, BigInteger.valueOf(index), destAddr, category == IBTP.Category.REQUEST
                , bizCallData, BigInteger.ZERO, 10000000L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
        log.info("tx hash:{}", transactionReceipt.getTransactionHash());
        return transactionReceipt;
    }

    public TransactionReceipt invokeInterchainWithError(String from, Long index, Boolean status) throws Exception {
        long currentHeight = client.appBlockNumber()
                .send().getBlockNumber().longValue();
        long validUntilBlock = currentHeight + 80;
        String nonce = CITAUtils.getNonce();
        RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = broker.invokeIndexUpdateWithError(from, BigInteger.valueOf(index),
                status, "", 10000000L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
        log.info("tx hash:{}", transactionReceipt.getTransactionHash());
        return transactionReceipt;
    }


    public IBTP generateCallBack(IBTP origin, byte[][] args, boolean status) throws InvalidProtocolBufferException {
        payload payload = pb.payload.parseFrom(origin.getPayload());
        content content = pb.content.parseFrom(payload.getContent());

        IBTP.Type typ = IBTP.Type.RECEIPT_SUCCESS;
        pb.content newContent = pb.content.newBuilder()
                .setSrcContractId(content.getDstContractId())
                .setDstContractId(content.getSrcContractId())
                .build();
        if (status) {
            pb.content.Builder builder = newContent.toBuilder().setFunc(content.getCallback());
            for (ByteString argCb : content.getArgsCbList()) {
                builder = builder.addArgs(argCb);
            }
            for (byte[] arg : args) {
                builder = builder.addArgs(ByteString.copyFrom(arg));

            }
            newContent = builder.build();
        } else {
            typ = IBTP.Type.RECEIPT_FAILURE;
            pb.content.Builder builder = newContent.toBuilder().setFunc(content.getRollback());
            for (ByteString argRb : content.getArgsRbList()) {
                builder = builder.addArgs(argRb);
            }
            newContent = builder.build();
        }
        pb.payload newPayload = pb.payload.newBuilder().setContent(newContent.toByteString()).build();
        return IBTP.newBuilder().setFrom(origin.getFrom())
                .setTo(origin.getTo())
                .setIndex(origin.getIndex())
                .setType(typ)
                .setTimestamp(System.currentTimeMillis())
                .setProof(origin.getProof())
                .setPayload(newPayload.toByteString())
                .setVersion(origin.getVersion())
                .build();
    }

}
