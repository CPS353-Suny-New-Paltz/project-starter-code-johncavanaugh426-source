package project.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessComputeServer {

    private Server server;

    private void start() throws IOException {
        int port = 50052; // port for the process/data-store service

        // Build and start the gRPC server
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new ProcessComputeServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        System.out.println("ProcessComputeServer started on port " + port);

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down ProcessComputeServer since JVM is shutting down");
            try {
                ProcessComputeServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** ProcessComputeServer shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            System.out.println("ProcessComputeServer stopping...");
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting ProcessComputeServer...");
        ProcessComputeServer server = new ProcessComputeServer();
        server.start();
        server.blockUntilShutdown();
    }
}
