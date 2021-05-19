package com.dmlab.cita.server.service;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.request.AppFilter;
import com.citahub.cita.protocol.core.methods.response.AppVersion;
import com.citahub.cita.protocol.http.HttpService;
import com.dmlab.cita.server.config.CitaConfig;
import com.moandjiezana.toml.Toml;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import pb.AppchainPluginGrpc.AppchainPluginImplBase;
import pb.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class AppchainPluginServiceImpl extends AppchainPluginImplBase {

    private CITAj client;
    private CitaConfig config;


    @Override
    public void initialize(InitializeRequest request, StreamObserver<Empty> responseObserver) {
        String configPath = request.getConfigPath();
        try {
            Toml toml = new Toml().read(Files.newInputStream(Path.of(configPath, "cita.toml")));
            String addr = toml.getString("addr", "https://testnet.citahub.com");
            String name = toml.getString("name", "cita");
            String contractAddress = toml.getString("contract_address");
            String key = toml.getString("key");
            Long minConfirm = toml.getLong("min_confirm");
            config = new CitaConfig(addr, name, contractAddress, key, minConfirm);
            responseObserver.onNext(Empty.newBuilder().build());
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }
        client = CITAj.build(new HttpService(config.getAddr()));
        try {
            AppVersion.Version version = client.getVersion().send().getVersion();
            log.info("Cita version:{}", version.softwareVersion);
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void start(Empty request, StreamObserver<Empty> responseObserver) {
        client.appGetLogs(new AppFilter());
    }

    @Override
    public void stop(Empty request, StreamObserver<Empty> responseObserver) {
        super.stop(request, responseObserver);
    }

    @Override
    public void getIBTP(Empty request, StreamObserver<IBTP> responseObserver) {
        super.getIBTP(request, responseObserver);
    }

    @Override
    public void submitIBTP(IBTP request, StreamObserver<SubmitIBTPResponse> responseObserver) {
        super.submitIBTP(request, responseObserver);
    }

    @Override
    public void getOutMessage(GetOutMessageRequest request, StreamObserver<IBTP> responseObserver) {
        super.getOutMessage(request, responseObserver);
    }

    @Override
    public void getInMessage(GetInMessageRequest request, StreamObserver<GetInMessageResponse> responseObserver) {
        super.getInMessage(request, responseObserver);
    }

    @Override
    public void getInMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        super.getInMeta(request, responseObserver);
    }

    @Override
    public void getOutMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        super.getOutMeta(request, responseObserver);
    }

    @Override
    public void getCallbackMeta(Empty request, StreamObserver<GetMetaResponse> responseObserver) {
        super.getCallbackMeta(request, responseObserver);
    }

    @Override
    public void commitCallback(IBTP request, StreamObserver<Empty> responseObserver) {
        super.commitCallback(request, responseObserver);
    }

    @Override
    public void getReceipt(IBTP request, StreamObserver<IBTP> responseObserver) {
        super.getReceipt(request, responseObserver);
    }

    @Override
    public void name(Empty request, StreamObserver<NameResponse> responseObserver) {
        responseObserver.onNext(NameResponse.newBuilder().setName(config.getName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void type(Empty request, StreamObserver<TypeResponse> responseObserver) {
        TypeResponse reply = TypeResponse.newBuilder().setType("cita").build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
