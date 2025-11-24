package project.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UserComputeServiceImpl extends UserComputeServiceGrpc.UserComputeServiceImplBase {

    private final ManagedChannel channel;
    private final ProcessComputeServiceGrpc.ProcessComputeServiceBlockingStub stub;
    private final ExecutorService executor;

    public UserComputeServiceImpl(String target) {
        channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        stub = ProcessComputeServiceGrpc.newBlockingStub(channel);
        executor = Executors.newFixedThreadPool(8); // THREAD_LIMIT = 8
        System.out.println("UserComputeServiceImpl: Connected to process server at " + target);
    }

    @Override
    public void processInput(UserComputeRequestMessage request,
                             StreamObserver<UserComputeResultMessage> responseObserver) 
    {
        executor.submit(() -> {
            System.out.println("UserComputeServiceImpl: Received request from client.");

            File inputFile = new File(request.getInputSource());
            if (!inputFile.exists()) {
                UserComputeResultMessage error = UserComputeResultMessage.newBuilder()
                        .setSuccess(false)
                        .setMessage("Input file does not exist: " + request.getInputSource())
                        .build();
                responseObserver.onNext(error);
                responseObserver.onCompleted();
                System.out.println("UserComputeServiceImpl: Input file missing.");
                return;
            }

            try {
                List<String> lines = Files.readAllLines(inputFile.toPath())
                                          .stream()
                                          .map(String::trim)
                                          .filter(s -> !s.isEmpty())
                                          .collect(Collectors.toList());
                if (lines.isEmpty()) {
                    UserComputeResultMessage error = UserComputeResultMessage.newBuilder()
                            .setSuccess(false)
                            .setMessage("Input file is empty: " + request.getInputSource())
                            .build();
                    responseObserver.onNext(error);
                    responseObserver.onCompleted();
                    System.out.println("UserComputeServiceImpl: Input file empty.");
                    return;
                }
            } 
            catch (IOException e) {
                UserComputeResultMessage error = UserComputeResultMessage.newBuilder()
                        .setSuccess(false)
                        .setMessage("Failed to read input file: " + e.getMessage())
                        .build();
                responseObserver.onNext(error);
                responseObserver.onCompleted();
                System.out.println("UserComputeServiceImpl: Failed to read input file.");
                return;
            }

            // Forward request to process server
            ProcessDataRequest processRequest = ProcessDataRequest.newBuilder()
                    .setInputSource(request.getInputSource())
                    .setOutputDestination(request.getOutputDestination())
                    .setOutputDelimiter(request.getOutputDelimiter())
                    .build();

            System.out.println("UserComputeServiceImpl: Forwarding request to process server...");
            ProcessDataResult result = stub.processData(processRequest);

            UserComputeResultMessage response = UserComputeResultMessage.newBuilder()
                    .setSuccess(result.getSuccess())
                    .setMessage(result.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            System.out.println("UserComputeServiceImpl: Sent response back to client.");
        });
    }

    public void shutdown() throws InterruptedException
    {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }
        executor.shutdown();
    }
}
