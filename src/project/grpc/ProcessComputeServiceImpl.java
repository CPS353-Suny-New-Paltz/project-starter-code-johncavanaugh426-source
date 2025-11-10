package project.grpc;

import io.grpc.stub.StreamObserver;
import project.impl.process.DataStorageComputeAPIImpl;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

public class ProcessComputeServiceImpl extends ProcessComputeServiceGrpc.ProcessComputeServiceImplBase {

    private final DataStorageComputeAPIImpl dataStore;

    public ProcessComputeServiceImpl() {
        this.dataStore = new DataStorageComputeAPIImpl();
        System.out.println("ProcessComputeServiceImpl: Data store service initialized.");
    }

    @Override
    public void processData(ProcessDataRequest request,
                            StreamObserver<ProcessDataResult> responseObserver) {
        System.out.println("ProcessComputeServiceImpl: Received request from UserComputeServer.");

        // Wrap request into ProcessRequest
        ProcessRequest processRequest = new ProcessRequest() {
            @Override
            public java.util.List<Integer> getInputData() {
                return null;
            }

            @Override
            public String getOutputDestination() {
                return request.getOutputDestination();
            }

            @Override
            public String getDelimiter() {
                return request.getOutputDelimiter().isEmpty() ? "," : request.getOutputDelimiter();
            }

            @Override
            public String getComputedResults() {
                return null;
            }

            @Override
            public String getInputSource() {
                return request.getInputSource();
            }
        };

        System.out.println("ProcessComputeServiceImpl: Calling DataStorageComputeAPIImpl...");

        // This call now blocks until all threads finish and file is fully written
        ProcessResult result = dataStore.processData(processRequest);

        System.out.println("ProcessComputeServiceImpl: Finished processing. Building response...");

        ProcessDataResult grpcResponse = ProcessDataResult.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .build();

        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();

        System.out.println("ProcessComputeServiceImpl: Response sent to UserComputeServer.");
    }
}
