package project.grpc;

import io.grpc.stub.StreamObserver;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.FastComputeEngineAPIImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProcessComputeServiceImpl extends ProcessComputeServiceGrpc.ProcessComputeServiceImplBase {

    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;

    public ProcessComputeServiceImpl() {
        this.dataStore = new DataStorageComputeAPIImpl();
        this.computeEngine = new FastComputeEngineAPIImpl();
        System.out.println("ProcessComputeServiceImpl: Initialized Fast Compute Engine and Data Store.");
    }

    @Override
    public void processData(ProcessDataRequest request, StreamObserver<ProcessDataResult> responseObserver) {
        System.out.println("ProcessComputeServiceImpl: Received request from user server.");

        try {
            // --- FILE READING MOVED TO DATA STORAGE API ---
            List<String> inputLines = dataStore.readInputFile(request.getInputSource());

            if (inputLines.isEmpty()) {
                responseObserver.onNext(ProcessDataResult.newBuilder()
                    .setSuccess(false)
                    .setMessage("Input file is empty")
                    .build());
                responseObserver.onCompleted();
                return;
            }

            String delimiter = request.getOutputDelimiter().isEmpty() ? "," : request.getOutputDelimiter();

            // Map each line to a ComputeRequest
            Map<Integer, ComputeRequest> indexedRequests = IntStream.range(0, inputLines.size())
                .boxed()
                .collect(Collectors.toMap(i -> i, i -> new ComputeRequest() {
                    @Override
                    public int getInputNumber() {
                        return 0; // unused
                    }

                    @Override
                    public String getInputString() {
                        return inputLines.get(i);
                    }
                }));

            // Compute sequences using FastComputeEngineAPI
            List<ComputeResult> results = ((FastComputeEngineAPIImpl) computeEngine)
                .computeCollatzBatch(indexedRequests.values().stream().collect(Collectors.toList()));

            // Build output in original order
            StringBuilder computedResults = new StringBuilder();
            for (int i = 0; i < inputLines.size(); i++) {
                ComputeResult cr = results.get(i);
                if (!cr.isSuccess()) {
                    throw new RuntimeException("Computation failed: " + cr.getSequence());
                }
                computedResults.append(cr.getSequence().replace(",", delimiter)).append("\n");
            }

            // Send results to Data Storage
            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public List<Integer> getInputData() {
                    return null;
                }

                @Override
                public String getOutputDestination() {
                    return request.getOutputDestination();
                }

                @Override
                public String getDelimiter() {
                    return delimiter;
                }

                @Override
                public String getComputedResults() {
                    return computedResults.toString();
                }

                @Override
                public String getInputSource() {
                    return request.getInputSource();
                }
            };

            ProcessResult result = dataStore.processData(processRequest);

            // Build and send gRPC response
            ProcessDataResult grpcResponse = ProcessDataResult.newBuilder()
                .setSuccess(result.isSuccess())
                .setMessage(result.getMessage())
                .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

            System.out.println("ProcessComputeServiceImpl: Response sent to user server.");

        } catch (Exception e) {
            responseObserver.onNext(ProcessDataResult.newBuilder()
                .setSuccess(false)
                .setMessage("Error in Compute Engine: " + e.getMessage())
                .build());
            responseObserver.onCompleted();
            System.err.println("ProcessComputeServiceImpl: Exception - " + e.getMessage());
        }
    }
}
