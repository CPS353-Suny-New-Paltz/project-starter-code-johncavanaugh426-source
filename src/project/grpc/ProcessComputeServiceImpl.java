package project.grpc;

import io.grpc.stub.StreamObserver;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;
import project.api.conceptual.ComputeEngineAPI;
import project.impl.conceptual.ComputeEngineAPIImpl;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessComputeServiceImpl extends ProcessComputeServiceGrpc.ProcessComputeServiceImplBase {

    private final DataStorageComputeAPIImpl dataStore;
    private final ComputeEngineAPI computeEngine;

    public ProcessComputeServiceImpl() {
        this.dataStore = new DataStorageComputeAPIImpl();
        this.computeEngine = new ComputeEngineAPIImpl();
        System.out.println("ProcessComputeServiceImpl: Initialized Compute Engine and Data Store.");
    }

    @Override
    public void processData(ProcessDataRequest request, StreamObserver<ProcessDataResult> responseObserver) {
        System.out.println("ProcessComputeServiceImpl: Received request from user server.");

        try {
            // Read input file lines
            List<String> inputLines = Files.lines(Paths.get(request.getInputSource()))
                                           .map(String::trim)
                                           .filter(s -> !s.isEmpty())
                                           .collect(Collectors.toList());

            if (inputLines.isEmpty()) {
                responseObserver.onNext(ProcessDataResult.newBuilder()
                        .setSuccess(false)
                        .setMessage("Input file is empty")
                        .build());
                responseObserver.onCompleted();
                return;
            }

            String delimiter = request.getOutputDelimiter().isEmpty() ? "," : request.getOutputDelimiter();

            // Compute sequences using ComputeEngineAPIImpl
            StringBuilder computedResults = new StringBuilder();
            for (String line : inputLines) {
                int number = Integer.parseInt(line);
                ComputeRequest computeRequest = () -> number;
                ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                if (!computeResult.isSuccess()) {
                    throw new RuntimeException("Computation failed for number: " + number);
                }

                computedResults.append(computeResult.getSequence().replace(",", delimiter))
                               .append("\n");
            }

            // Send computed results to Data Storage
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
