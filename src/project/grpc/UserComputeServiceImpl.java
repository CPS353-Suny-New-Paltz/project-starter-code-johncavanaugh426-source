package project.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import project.api.process.DataStorageComputeAPI;
import project.impl.process.DataStorageComputeAPIImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserComputeServiceImpl extends UserComputeServiceGrpc.UserComputeServiceImplBase {

    private final ManagedChannel channel;
    private final ProcessComputeServiceGrpc.ProcessComputeServiceBlockingStub stub;
    private final ExecutorService executor;
    private final DataStorageComputeAPI dataStore;

    public UserComputeServiceImpl(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target)
            .usePlaintext()
            .build();
        this.stub = ProcessComputeServiceGrpc.newBlockingStub(channel);
        this.executor = Executors.newFixedThreadPool(8);
        this.dataStore = new DataStorageComputeAPIImpl();
        System.out.println("UserComputeServiceImpl: Connected to process server at " + target);
    }

    @Override
    public void processInput(UserComputeRequestMessage request, StreamObserver<UserComputeResultMessage> responseObserver) {
        executor.submit(() -> {
            System.out.println("UserComputeServiceImpl: Received request from client.");

            try {
                // --- FILE READING MOVED TO DATA STORAGE API ---
                dataStore.readInputFile(request.getInputSource());

            } catch (Exception e) {
                UserComputeResultMessage error = UserComputeResultMessage.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to read input file: " + e.getMessage())
                    .build();
                responseObserver.onNext(error);
                responseObserver.onCompleted();
                return;
            }

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

    public void shutdown() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }
        executor.shutdown();
    }
}
