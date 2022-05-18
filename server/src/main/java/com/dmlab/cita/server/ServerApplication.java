package com.dmlab.cita.server;

import com.dmlab.cita.server.service.AppchainPluginServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ServerApplication {
    private Server server;
    private static final int protoVersion = 4;

    @Value("${grpc.server.port}")
    private int port;


    private void start() throws IOException {
        /* The port on which the server should run */
        server = ServerBuilder.forPort(port)
                .addService(new AppchainPluginServiceImpl())
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    ServerApplication.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });

        System.out.println("1|" + protoVersion + "|tcp|127.0.0.1:" + port + "|grpc");
        System.out.flush();
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        final ServerApplication server = new ServerApplication();
        server.start();
        server.blockUntilShutdown();
    }

}
