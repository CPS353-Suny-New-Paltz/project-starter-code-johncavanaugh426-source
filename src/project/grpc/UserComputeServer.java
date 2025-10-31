package project.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UserComputeServer {

    private Server server;

    // Start gRPC server on port 50051
    private void start() throws IOException {
        int port = 50051;

        // Pass process server target to the service
        UserComputeServiceImpl userService = new UserComputeServiceImpl("localhost:50052");

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(userService)
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        System.out.println("UserComputeServer started on port " + port);

        // Handle server shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                stop();
                userService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    // Stop server 
    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    // Keep main thread alive
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    // Entry point
    public static void main(String[] args) throws Exception {
        UserComputeServer server = new UserComputeServer();
        server.start();
        server.blockUntilShutdown();
    }
}
