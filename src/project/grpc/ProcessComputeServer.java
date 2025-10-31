package project.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessComputeServer {

    private Server server;

    private void start() throws IOException {
        int port = 50052;

        server = NettyServerBuilder.forPort(port)
                .addService(new ProcessComputeServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        System.out.println("ProcessComputeServer started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down gRPC server since JVM is shutting down");
            try {
                if (server != null) {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** Server shut down");
        }));
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        ProcessComputeServer server = new ProcessComputeServer();
        server.start();
        server.blockUntilShutdown();
    }
}
