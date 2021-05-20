package com.dmlab.cita.server.service;

import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.*;
import com.citahub.cita.abi.datatypes.generated.Uint256;
import com.citahub.cita.abi.datatypes.generated.Uint64;
import com.citahub.cita.crypto.Credentials;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.RemoteCall;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tuples.generated.Tuple2;
import com.citahub.cita.tx.RawTransactionManager;
import com.citahub.cita.tx.TransactionManager;
import com.dmlab.cita.server.config.CitaConfig;
import com.dmlab.cita.server.contracts.Broker;
import com.dmlab.cita.server.utils.CITAUtils;
import com.dmlab.cita.server.utils.IBTPUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.moandjiezana.toml.Toml;
import io.grpc.stub.StreamObserver;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;
import pb.AppchainPluginGrpc.AppchainPluginImplBase;
import pb.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NoSuchObjectException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

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


    @Override
    public void initialize(InitializeRequest request, StreamObserver<Empty> responseObserver) {
        String configPath = request.getConfigPath();
        pierId = request.getPierId();
        try {
            Toml toml = new Toml().read(Files.newInputStream(Path.of(configPath, "cita.toml")));
            String addr = toml.getString("addr", "https://testnet.citahub.com");
            String name = toml.getString("name", "cita");
            String contractAddress = toml.getString("contract_address");
            String key = toml.getString("key");
            Long minConfirm = toml.getLong("min_confirm");
            config = new CitaConfig(addr, name, contractAddress, key, minConfirm);
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        client = CITAj.build(new HttpService(config.getAddr()));
        try {
            version = CITAUtils.getVersion(client);
            chainId = CITAUtils.getChainId(client);
            log.info("Cita version:{}, chainId:{}", version, chainId.intValue());
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }

        TransactionManager citaTxManager = new RawTransactionManager(
                client, Credentials.create(config.getPrivateKey()), 5, 3000);

        broker = Broker.load(config.getContractAddress(), client, citaTxManager);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void start(Empty request, StreamObserver<Empty> responseObserver) {
        Flowable<Broker.ThrowEventEventResponse> flowable = broker.throwEventEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.PENDING);
        flowable.subscribe(new Consumer<Broker.ThrowEventEventResponse>() {
            @Override
            public void accept(Broker.ThrowEventEventResponse throwEventEventResponse) throws Exception {
                eventC.put(IBTPUtils.convertFromEvent(throwEventEventResponse, pierId));
            }
        });

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
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
                e.printStackTrace();
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
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }

        if (IBTPUtils.category(request) == IBTP.Category.UNKNOWN) {
            responseObserver.onError(new IllegalArgumentException("invalid ibtp category"));
            return;
        }

        if (IBTPUtils.category(request) == IBTP.Category.RESPONSE && content.getFunc().equals("")) {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = invokeInterchainWithError(request.getFrom(), request.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                responseObserver.onError(e);
                return;
            }
            boolean status = transactionReceipt.getStatus().equalsIgnoreCase("1");
            if (!status) {
                responseObserver.onError(new IllegalStateException("update index for ibtp failed" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
            sir = sir.toBuilder().setStatus(true).build();
            responseObserver.onNext(sir);
            return;
        }

        //TODO:need to handle abi encode error
        ArrayList<DynamicBytes> dynamicBytesArrayList = new ArrayList<>();
        content.getArgsList().forEach(arg -> {
            dynamicBytesArrayList.add(new DynamicBytes(arg.toByteArray()));
        });
        Function func = new Function(
                content.getFunc(),
                Arrays.asList(new DynamicArray(dynamicBytesArrayList.toArray(new Type[]{}))),
                Collections.emptyList()
        );
        String funcEncoder = FunctionEncoder.encode(func);

        TransactionReceipt receipt = null;
        try {
            receipt = invokeInterchain(request.getFrom(), request.getIndex(), request.getTo(), IBTPUtils.category(request), funcEncoder.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }

        Broker.ThrowEventEventResponse throwEventEventResponse = null;
        boolean status = receipt.getStatus().equalsIgnoreCase("1");
        if (status) {
            List<Broker.ThrowEventEventResponse> throwEventEvents = broker.getThrowEventEvents(receipt);
            if (throwEventEvents.size() > 1 || throwEventEvents.isEmpty()) {
                responseObserver.onError(new NoSuchObjectException("not found ThrowEventEvent"));
                return;
            }
            throwEventEventResponse = throwEventEvents.get(0);
        } else {
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = invokeInterchainWithError(request.getFrom(), request.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                responseObserver.onError(e);
                return;
            }
            status = transactionReceipt.getStatus().equalsIgnoreCase("1");
            if (!status) {
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
        byte[][] args = new byte[][]{};
        List<byte[]> collect = Arrays.stream(throwEventEventResponse.argscb.split(",")).map(String::getBytes).collect(Collectors.toList());
        IBTP callBack = null;
        try {
            callBack = generateCallBack(request, collect.toArray(args), sir.getStatus());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        sir = sir.toBuilder().setResult(callBack).build();
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
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }

        if (!content.getFunc().equals("interchainCharge")) {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
            return;
        }

        Function func = new Function(content.getFunc(), Arrays.asList(
                new Utf8String(content.getArgsRb(0).toString()),
                new Utf8String(content.getArgsRb(1).toString()),
                new Uint64(Long.parseLong(content.getArgsRb(2).toString()))
        ), Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
        }));
        String encode = FunctionEncoder.encode(func);
        TransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = invokeInterchain(request.getIbtp().getTo(), request.getIbtp().getIndex(), content.getSrcContractId(), IBTPUtils.category(request.getIbtp()), encode.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;

        }
        boolean status = transactionReceipt.getStatus().equalsIgnoreCase("1");
        if (!status) {
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
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        try {
            TransactionReceipt transactionReceipt = invokeInterchainWithError(ibtp.getFrom(), ibtp.getIndex(), IBTPUtils.category(request) == IBTP.Category.REQUEST);
            boolean status = transactionReceipt.getStatus().equalsIgnoreCase("1");
            if (!status) {
                responseObserver.onError(new IllegalStateException("invalid index for ibtp:" + request.getFrom() + "-" + request.getTo() + "-" + request.getIndex()));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        responseObserver.onNext(ibtp);
        responseObserver.onCompleted();
    }

    @Override
    public void getOutMessage(GetOutMessageRequest request, StreamObserver<IBTP> responseObserver) {
        try {
            BigInteger block = broker.getOutMessage(request.getTo(), BigInteger.valueOf(request.getIdx())).send();
            Flowable<Broker.ThrowEventEventResponse> flowable = broker.throwEventEventFlowable(DefaultBlockParameter.valueOf(block), DefaultBlockParameter.valueOf(block));

            flowable.subscribe(new Subscriber<Broker.ThrowEventEventResponse>() {
                private IBTP ibtp = null;

                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Broker.ThrowEventEventResponse throwEventEventResponse) {
                    if (ibtp != null) {
                        return;
                    }

                    String[] split = throwEventEventResponse.destDID.split(":");
                    if (split.length != 4 || !"did".equals(split[0]) || "".equals(split[1]) || "".equals(split[2]) || "".equals(split[3])) {
                        return;
                    }

                    String destchainid = String.join(":", split[0], split[1], split[2], ".");
                    if (destchainid.equals(request.getTo()) && throwEventEventResponse.index.longValue() == request.getIdx()) {
                        try {
                            ibtp = IBTPUtils.convertFromEvent(throwEventEventResponse, pierId);
                            responseObserver.onNext(ibtp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            responseObserver.onError(e);
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    responseObserver.onError(throwable);
                }

                @Override
                public void onComplete() {
                    if (ibtp == null) {
                        responseObserver.onError(new Exception("no ibtp with to " + request.getTo() + " and index " + request.getIdx() + " found"));
                    }
                    responseObserver.onCompleted();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getInMessage(GetInMessageRequest request, StreamObserver<GetInMessageResponse> responseObserver) {
        BigInteger inBlock = null;
        try {
            inBlock = broker.getInMessage(request.getFrom(), BigInteger.valueOf(request.getIdx())).send();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
        Future<BigInteger> bigIntegerFuture = broker.getInMessage(request.getFrom(), BigInteger.valueOf(request.getIndex())).sendAsync();
        BigInteger blockNum = null;
        try {
            blockNum = bigIntegerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        Flowable<Broker.ThrowEventEventResponse> throwEventEventResponseFlowable = broker.throwEventEventFlowable(DefaultBlockParameter.valueOf(blockNum), DefaultBlockParameter.valueOf(blockNum));
        throwEventEventResponseFlowable.subscribe(new Subscriber<Broker.ThrowEventEventResponse>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Broker.ThrowEventEventResponse event) {
                String[] split = event.destDID.split(":");
                if (split.length != 4 || !"did".equals(split[0]) || "".equals(split[1]) || "".equals(split[2]) || "".equals(split[3])) {
                    return;
                }

                String destChainId = String.join(":", split[0], split[1], split[2], ".");
                if (destChainId.equalsIgnoreCase(request.getTo()) && event.index.compareTo(BigInteger.valueOf(request.getIndex())) == 0) {
                    byte[][] args = new byte[][]{};
                    List<byte[]> collect = Arrays.stream(event.argscb.split(",")).map(String::getBytes).collect(Collectors.toList());
                    IBTP ibtp = null;
                    try {
                        ibtp = generateCallBack(request, collect.toArray(args), true);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                        responseObserver.onError(e);
                        return;
                    }
                    responseObserver.onNext(ibtp);
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onComplete() {
                try {
                    IBTP ibtp = generateCallBack(request, null, false);
                    responseObserver.onNext(ibtp);
                    responseObserver.onCompleted();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    responseObserver.onError(e);
                }
            }
        });
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
                , bizCallData, BigInteger.ZERO, 1L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        return transactionReceiptFuture.get();
    }

    public TransactionReceipt invokeInterchainWithError(String from, Long index, Boolean status) throws Exception {
        long currentHeight = client.appBlockNumber()
                .send().getBlockNumber().longValue();
        long validUntilBlock = currentHeight + 80;
        String nonce = CITAUtils.getNonce();
        RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = broker.invokeIndexUpdateWithError(from, BigInteger.valueOf(index),
                status, "", 1L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        return transactionReceiptFuture.get();
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
            for (int i = 0; i < content.getArgsCbCount(); i++) {
                builder = builder.setArgs(i, content.getArgsCb(i));
            }
            for (int i = 0; i < args.length; i++) {
                builder = builder.setArgs(i, ByteString.copyFrom(args[i]));
            }
            newContent = builder.build();
        } else {
            typ = IBTP.Type.RECEIPT_FAILURE;
            pb.content.Builder builder = newContent.toBuilder().setFunc(content.getRollback());
            for (int i = 0; i < content.getArgsRbCount(); i++) {
                builder = builder.setArgs(i, content.getArgsRb(i));
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
