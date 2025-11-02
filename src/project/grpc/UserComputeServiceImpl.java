package project.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

public class UserComputeServiceImpl extends UserComputeServiceGrpc.UserComputeServiceImplBase {

    private final ManagedChannel channel;
    private final ProcessComputeServiceGrpc.ProcessComputeServiceBlockingStub stub;

    // Connect to process server on localhost:50052
    public UserComputeServiceImpl(String target) {
        channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        stub = ProcessComputeServiceGrpc.newBlockingStub(channel);
        System.out.println("UserComputeServiceImpl: Connected to process server at " + target);
    }

    // Handle incoming user compute requests
    @Override
    public void processInput(UserComputeRequestMessage request,
                             StreamObserver<UserComputeResultMessage> responseObserver) {

        System.out.println("UserComputeServiceImpl: Received request from client.");

        // Build process request for the data store
        ProcessDataRequest processRequest = ProcessDataRequest.newBuilder()
                .setInputSource(request.getInputSource())
                .setOutputDestination(request.getOutputDestination())
                .setOutputDelimiter(request.getOutputDelimiter())
                .build();

        System.out.println("UserComputeServiceImpl: Forwarding request to process server...");

        // Call process server
        ProcessDataResult result = stub.processData(processRequest);

        System.out.println("UserComputeServiceImpl: Received response from process server.");

        // Build and send response to user
        UserComputeResultMessage response = UserComputeResultMessage.newBuilder()
                .setSuccess(result.getSuccess())
                .setMessage(result.getMessage())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        System.out.println("UserComputeServiceImpl: Sent response back to client.");
    }

    // close the channel
    public void shutdown() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
