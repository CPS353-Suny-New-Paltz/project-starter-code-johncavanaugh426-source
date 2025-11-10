package project.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UserComputeServer {

    private Server server;

    private void start() throws IOException {
        int port = 50051;

        // UserComputeServiceImpl connects to process server
        UserComputeServiceImpl userService = new UserComputeServiceImpl("localhost:50052");

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(userService)
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        System.out.println("UserComputeServer started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down UserComputeServer since JVM is shutting down");
            try {
                stop();
                userService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** UserComputeServer shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        UserComputeServer server = new UserComputeServer();
        server.start();
        server.blockUntilShutdown();
    }
}
