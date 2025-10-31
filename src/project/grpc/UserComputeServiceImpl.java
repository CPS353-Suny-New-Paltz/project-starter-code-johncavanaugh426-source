package project.grpc;

import io.grpc.stub.StreamObserver;
import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;

public class UserComputeServiceImpl extends UserComputeServiceGrpc.UserComputeServiceImplBase {

    private final UserComputeAPI userComputeAPI;

    public UserComputeServiceImpl() {
        this.userComputeAPI = new UserComputeAPIImpl();
    }

    @Override
    public void processInput(UserComputeRequestMessage request, StreamObserver<UserComputeResultMessage> responseObserver) {
        // Wrap protobuf request into the internal UserComputeRequest
        UserComputeRequest internalRequest = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return request.getInputSource();
            }

            @Override
            public String getOutputDelimiter() {
                return request.hasOutputDelimiter() ? request.getOutputDelimiter() : ",";
            }

            @Override
            public String getOutputDestination() {
                return request.getOutputDestination();
            }
        };

        // Call your API
        UserComputeResult result = userComputeAPI.processInput(internalRequest);

        // Wrap internal result into protobuf
        UserComputeResultMessage response = UserComputeResultMessage.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .build();

        // Send response
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
