package com.dmlab.cita.server.service;

import com.citahub.cita.abi.datatypes.Utf8String;
import com.citahub.cita.crypto.Credentials;
import com.citahub.cita.crypto.sm2.SM2;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.RemoteCall;
import com.citahub.cita.protocol.core.methods.response.*;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tuples.generated.Tuple2;
import com.citahub.cita.tuples.generated.Tuple3;
import com.citahub.cita.tx.RawTransactionManager;
import com.citahub.cita.tx.TransactionManager;
import com.dmlab.cita.server.config.CitaConfig;
import com.dmlab.cita.server.contracts.Broker;
import com.dmlab.cita.server.utils.CITAUtils;
import com.dmlab.cita.server.utils.IBTPUtils;
import com.google.protobuf.ByteString;
import com.moandjiezana.toml.Toml;
import io.grpc.stub.*;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Hash;
import pb.AppchainPluginGrpc.AppchainPluginImplBase;
import pb.*;
import pb.Ibtp.*;
import pb.Plugin.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

@GrpcService
@Component
@Slf4j
public class AppchainPluginServiceImpl extends AppchainPluginImplBase {

    private static String TYPE = "cita";
    private CITAj client;
    private CitaConfig config;
    private int version;
    private BigInteger chainId;

    private static BlockingQueue<Ibtp.IBTP> eventC = new ArrayBlockingQueue<Ibtp.IBTP>(1024);
    private Broker broker;


    @Override
    public void initialize(InitializeRequest request, StreamObserver<Empty> responseObserver) {
        String configPath = request.getConfigPath();
        try {
            Toml toml = new Toml().read(Files.newInputStream(FileSystems.getDefault().getPath(configPath, "cita.toml")));
            String addr = toml.getString("addr", "https://testnet.citahub.com");
            System.out.println(addr);
            String name = toml.getString("name", "cita");
            String contractAddress = toml.getString("contract_address");
            Long timeoutHeight = toml.getLong("timeout_height");
            String algo = toml.getString("algo");
            String key = toml.getString("key");
            config = new CitaConfig(addr, name, contractAddress, algo, key, timeoutHeight);
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        client = CITAj.build(new HttpService(config.getAddr()));
        System.out.println(config);
        try {
            version = CITAUtils.getVersion(client);
            chainId = CITAUtils.getChainId(client);
            log.info("Cita version:{}, chainId:{}", version, chainId.intValue());
        } catch (IOException e) {
            e.printStackTrace();
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

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void start(Empty request, StreamObserver<Empty> responseObserver) {
        Runnable runnable = () -> {
            BigInteger startBlock = getCurBlockNumber();

            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BigInteger endBlock = getCurBlockNumber();

                if (endBlock.compareTo(startBlock) < 0) {
                    log.warn("cur block is smaller than start block, start block: {}, end block: {}", startBlock.toString(), endBlock.toString());
                    continue;
                }

                log.info("start block: {}, end block: {}", startBlock.toString(), endBlock.toString());
                Flowable<Broker.ThrowInterchainEventEventResponse> throwInterchainEventEventResponseFlowable = broker.
                        throwInterchainEventEventFlowable(DefaultBlockParameter.valueOf(startBlock), DefaultBlockParameter.valueOf(endBlock));
                throwInterchainEventEventResponseFlowable.blockingSubscribe(new Consumer<Broker.ThrowInterchainEventEventResponse>() {
                    @Override
                    public void accept(Broker.ThrowInterchainEventEventResponse throwInterchainEventEventResponse) throws Exception {
                        try {
                            eventC.put(convertFromEvent(throwInterchainEventEventResponse));
                        } catch (Exception e) {
                            log.info(e.getMessage());
                        }
                    }
                });


                Flowable<Broker.ThrowReceiptEventEventResponse> throwReceiptEventEventResponseFlowable = broker.throwReceiptEventEventFlowable(DefaultBlockParameter.valueOf(startBlock), DefaultBlockParameter.valueOf(endBlock));
                throwReceiptEventEventResponseFlowable.blockingSubscribe(new Consumer<Broker.ThrowReceiptEventEventResponse>() {
                    @Override
                    public void accept(Broker.ThrowReceiptEventEventResponse throwReceiptEventEventResponse) throws Exception {
                        try {
                            eventC.put(convertFromEvent(throwReceiptEventEventResponse));
                        } catch (Exception e) {
                            log.info(e.getMessage());
                        }
                    }
                });


                startBlock = endBlock.add(BigInteger.ONE);
            }
        };


        Thread thread = new Thread(runnable);
        thread.start();

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private IBTP convertFromEvent(Broker.ThrowReceiptEventEventResponse ev) {
        String servicePair = ev.srcFullID + "-" + ev.dstFullID;
        Tuple3<List<byte[]>, BigInteger, Boolean> out = null;
        try {
            out = broker.getReceiptMessage(servicePair, ev.index).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("listen receipt ibtp service_pair:{}, idx:{}, out{}", servicePair, ev.index.intValue(), out);

        result.Builder builder = result.newBuilder();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] bytes : out.getValue1()) {
            builder.addData(ByteString.copyFrom(bytes));
            try {
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result res = builder.build();
        payload pay = payload.newBuilder().setEncrypted(out.getValue3()).setContent(res.toByteString()).setHash(ByteString.copyFrom(Hash.sha3(output.toByteArray()))).build();

        IBTP ibtp = IBTP.newBuilder()
                .setFrom(ev.srcFullID)
                .setTo(ev.dstFullID)
                .setIndex(ev.index.intValue())
                .setPayload(pay.toByteString())
                .setProof(ByteString.copyFromUtf8("1"))
                .setTimeoutHeight(0).build();
        return ibtp;
    }

    private IBTP convertFromEvent(Broker.ThrowInterchainEventEventResponse ev) {
        String servicePair = ev.srcFullID + "-" + ev.dstFullID;
        Tuple3<String, List<byte[]>, Boolean> out = null;
        try {
            out = broker.getOutMessage(servicePair, ev.index).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("listen interchain ibtp service_pair:{}, idx:{}, out{}", servicePair, ev.index.intValue(), out);
        assert out != null;
        ev.func = out.getValue1();
        ev.args = out.getValue2();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] funcData = ev.func.getBytes(StandardCharsets.UTF_8);
        List<ByteString> argsByteString = new ArrayList<>();
        try {
            output.write(funcData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (byte[] arg : ev.args) {
            try {
                output.write(arg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            argsByteString.add(ByteString.copyFrom(arg));
        }
        ev.hash = Hash.sha3(output.toByteArray());

        content cont = content.newBuilder().setFunc(ev.func).addAllArgs(argsByteString).build();
        payload pay = payload.newBuilder().setContent(cont.toByteString()).setEncrypted(out.getValue3()).setHash(ByteString.copyFrom(ev.hash)).build();

        IBTP ibtp = IBTP.newBuilder()
                .setFrom(ev.srcFullID)
                .setTo(ev.dstFullID)
                .setIndex(ev.index.intValue())
                .setPayload(pay.toByteString())
                .setProof(ByteString.copyFromUtf8("1"))
                .setTimeoutHeight(config.getTimeoutHeight()).build();
        return ibtp;
    }


    private BigInteger getCurBlockNumber() {
        while (true) {
            try {
                return client.appBlockNumber().send().getBlockNumber();
            } catch (IOException e) {
                e.printStackTrace();
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
    public void getUpdateMeta(pb.Plugin.Empty request,
                              io.grpc.stub.StreamObserver<pb.Plugin.UpdateMeta> responseObserver) {
        responseObserver.onCompleted();
    }

    @Override
    public void getIBTPCh(pb.Plugin.Empty request,
                          io.grpc.stub.StreamObserver<pb.Ibtp.IBTP> responseObserver) {
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
    public void submitIBTP(pb.Plugin.SubmitIBTPRequest request,
                           io.grpc.stub.StreamObserver<pb.Plugin.SubmitIBTPResponse> responseObserver) {
        SubmitIBTPResponse sir = SubmitIBTPResponse.newBuilder().setStatus(true).build();
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = invokeInterchain(request);
        } catch (Exception e) {
            e.printStackTrace();
            sir.toBuilder().setStatus(false).setMessage(e.getMessage()).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }
        boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
        if (!status) {
            sir.toBuilder().setStatus(false).setMessage(transactionReceipt.getErrorMessage()).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(sir);
        responseObserver.onCompleted();
    }

    @Override
    public void submitReceipt(pb.Plugin.SubmitReceiptRequest request,
                              io.grpc.stub.StreamObserver<pb.Plugin.SubmitIBTPResponse> responseObserver) {
        SubmitIBTPResponse sir = SubmitIBTPResponse.newBuilder().setStatus(true).build();
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = invokeReceiptInterchain(request);
        } catch (Exception e) {
            e.printStackTrace();
            sir.toBuilder().setStatus(false).setMessage(e.getMessage()).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }
        boolean status = StringUtils.hasLength(transactionReceipt.getErrorMessage());
        if (!status) {
            sir.toBuilder().setStatus(false).setMessage(transactionReceipt.getErrorMessage()).build();
            responseObserver.onNext(sir);
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(sir);
        responseObserver.onCompleted();
    }

    @Override
    public void getOutMessage(pb.Plugin.GetMessageRequest request,
                              io.grpc.stub.StreamObserver<pb.Ibtp.IBTP> responseObserver) {
        String[] servicePair = request.getServicePair().split("-");
        if (servicePair.length != 2) {
            responseObserver.onError(new Exception("parse service pair fail:" + request.getServicePair()));
            responseObserver.onCompleted();
            return;
        }
        Broker.ThrowInterchainEventEventResponse ev = new Broker.ThrowInterchainEventEventResponse();
        ev.srcFullID = servicePair[0];
        ev.dstFullID = servicePair[1];
        ev.index = BigInteger.valueOf(request.getIdx());
        Tuple3<String, List<byte[]>, Boolean> out = null;
        try {
            out = broker.getOutMessage(request.getServicePair(), BigInteger.valueOf(request.getIdx())).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("get out ibtp service_pair:{}, idx:{}, out{}", request.getServicePair(), request.getIdx(), out);
        assert out != null;
        ev.func = out.getValue1();
        ev.args = out.getValue2();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] funcData = ev.func.getBytes(StandardCharsets.UTF_8);
        List<ByteString> argsByteString = new ArrayList<>();
        try {
            output.write(funcData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (byte[] arg : ev.args) {
            try {
                output.write(arg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            argsByteString.add(ByteString.copyFrom(arg));
        }
        ev.hash = Hash.sha3(output.toByteArray());

        content cont = content.newBuilder().setFunc(ev.func).addAllArgs(argsByteString).build();
        payload pay = payload.newBuilder().setContent(cont.toByteString()).setEncrypted(out.getValue3()).setHash(ByteString.copyFrom(ev.hash)).build();

        IBTP ibtp = IBTP.newBuilder()
                .setFrom(ev.srcFullID)
                .setTo(ev.dstFullID)
                .setIndex(request.getIdx())
                .setPayload(pay.toByteString())
                .setProof(ByteString.copyFromUtf8("1"))
                .setTimeoutHeight(config.getTimeoutHeight()).build();
        responseObserver.onNext(ibtp);
        responseObserver.onCompleted();
    }

    /**
     *
     */
    public void getReceiptMessage(pb.Plugin.GetMessageRequest request,
                                  io.grpc.stub.StreamObserver<pb.Ibtp.IBTP> responseObserver) {
        Tuple3<List<byte[]>, BigInteger, Boolean> out = null;
        try {
            out = broker.getReceiptMessage(request.getServicePair(), BigInteger.valueOf(request.getIdx())).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("get missing receipt ibtp service_pair:{}, idx:{}, out{}", request.getServicePair(), request.getIdx(), out);

        result.Builder builder = result.newBuilder();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] bytes : out.getValue1()) {
            builder.addData(ByteString.copyFrom(bytes));
            try {
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result res = builder.build();
        payload pay = payload.newBuilder().setEncrypted(out.getValue3()).setContent(res.toByteString()).setHash(ByteString.copyFrom(Hash.sha3(output.toByteArray()))).build();

        String[] servicePair = request.getServicePair().split("-");
        if (servicePair.length != 2) {
            responseObserver.onError(new Exception("parse service pair fail:" + request.getServicePair()));
            responseObserver.onCompleted();
            return;
        }

        IBTP ibtp = IBTP.newBuilder()
                .setFrom(servicePair[0])
                .setTo(servicePair[1])
                .setIndex(request.getIdx())
                .setPayload(pay.toByteString())
                .setProof(ByteString.copyFromUtf8("1"))
                .setTimeoutHeight(0).build();
        responseObserver.onNext(ibtp);
        responseObserver.onCompleted();

    }


    @Override
    public void getDstRollbackMeta(pb.Plugin.Empty request,
                                   io.grpc.stub.StreamObserver<pb.Plugin.GetMetaResponse> responseObserver) {
        Future<Tuple2<List<String>, List<BigInteger>>> tuple2Future = broker.getDstRollbackMeta().sendAsync();
        getMeta(responseObserver, tuple2Future);
    }

    @Override
    public void getServices(pb.Plugin.Empty request,
                            io.grpc.stub.StreamObserver<pb.Plugin.ServicesResponse> responseObserver) {
        List<Utf8String> localServiceList = null;
        try {
            localServiceList = broker.getLocalServiceList().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> services = new ArrayList<>();
        localServiceList.forEach(service ->{
            services.add(service.getValue());
        });
        responseObserver.onNext(ServicesResponse.newBuilder().addAllService(services).build());
        responseObserver.onCompleted();
    }

    /**
     *
     */
    @Override
    public void getChainID(pb.Plugin.Empty request,
                           io.grpc.stub.StreamObserver<pb.Plugin.ChainIDResponse> responseObserver) {
        Tuple2<String, String> chainID = null;
        try {
            chainID = broker.getChainID().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onNext(ChainIDResponse.newBuilder().setBxhID(chainID.getValue1()).setAppchainID(chainID.getValue2()).build());
        responseObserver.onCompleted();


    }

    /**
     *
     */
    @Override
    public void getAppchainInfo(pb.Plugin.ChainInfoRequest request,
                                io.grpc.stub.StreamObserver<pb.Plugin.ChainInfoResponse> responseObserver) {
        Tuple3<String, byte[], String> appchainInfo = null;
        try {
            appchainInfo = broker.getAppchainInfo(request.getChainID()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onNext(ChainInfoResponse.newBuilder()
                .setBroker(appchainInfo.getValue1()).setTrustedRoot(ByteString.copyFrom(appchainInfo.getValue2())).setRuleAddr(appchainInfo.getValue3()).build());
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
    public void name(Empty request, StreamObserver<NameResponse> responseObserver) {
        responseObserver.onNext(NameResponse.newBuilder().setName(config.getName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void type(Empty request, StreamObserver<TypeResponse> responseObserver) {
        responseObserver.onNext(TypeResponse.newBuilder().setType(TYPE).build());
        responseObserver.onCompleted();
    }


    public TransactionReceipt invokeInterchain(SubmitIBTPRequest request) throws Exception {
        long currentHeight = client.appBlockNumber()
                .send().getBlockNumber().longValue();
        long validUntilBlock = currentHeight + 80;
        String nonce = CITAUtils.getNonce();
        List<byte[]> args = new ArrayList<>();
        List<byte[]> multiSigs = new ArrayList<>();
        request.getContent().getArgsList().forEach(arg -> args.add(arg.toByteArray()));
        request.getBxhProof().getMultiSignList().forEach(arg -> multiSigs.add(arg.toByteArray()));
        RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = broker.invokeInterchain(
                request.getFrom(),
                request.getServiceId(),
                BigInteger.valueOf(request.getIndex()),
                BigInteger.valueOf(request.getType().getNumber()),
                request.getContent().getFunc(),
                args,
                BigInteger.valueOf(request.getBxhProof().getTxStatusValue()),
                multiSigs,
                request.getIsEncrypted(),
                BigInteger.ZERO, 10000000L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
        log.info("tx hash:{}", transactionReceipt.getTransactionHash());
        return transactionReceipt;
    }

    public TransactionReceipt invokeReceiptInterchain(SubmitReceiptRequest request) throws Exception {
        long currentHeight = client.appBlockNumber()
                .send().getBlockNumber().longValue();
        long validUntilBlock = currentHeight + 80;
        String nonce = CITAUtils.getNonce();
        List<byte[]> args = new ArrayList<>();
        request.getResult().getDataList().forEach(arg -> args.add(arg.toByteArray()));
        List<byte[]> multiSigs = new ArrayList<>();
        request.getBxhProof().getMultiSignList().forEach(arg -> multiSigs.add(arg.toByteArray()));
        RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = broker.invokeReceipt(
                request.getServiceId(),
                request.getTo(),
                BigInteger.valueOf(request.getIndex()),
                BigInteger.valueOf(request.getType().getNumber()),
                args,
                BigInteger.valueOf(request.getBxhProof().getTxStatusValue()),
                multiSigs,
                BigInteger.ZERO, 10000000L, nonce, validUntilBlock, version, chainId, "");
        Future<TransactionReceipt> transactionReceiptFuture = transactionReceiptRemoteCall.sendAsync();
        TransactionReceipt transactionReceipt = transactionReceiptFuture.get();
        log.info("tx hash:{}", transactionReceipt.getTransactionHash());
        return transactionReceipt;
    }


}
