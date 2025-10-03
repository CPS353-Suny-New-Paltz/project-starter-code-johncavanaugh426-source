package project.impl.network;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.ComputeEngineAPIImpl;
import project.impl.process.DataStorageComputeAPIImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class UserComputeAPIImpl implements UserComputeAPI {
    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;

    public UserComputeAPIImpl() {
        this(new DataStorageComputeAPIImpl(), new ComputeEngineAPIImpl());
    }

    public UserComputeAPIImpl(DataStorageComputeAPI dataStore, ComputeEngineAPI computeEngine) {
        this.dataStore = dataStore;
        this.computeEngine = computeEngine;
    }

    @Override
    public UserComputeResult processInput(UserComputeRequest request) {
        try {
            // Validation
            if (request == null) {
                return new UserComputeResult(false, "Request cannot be null");
            }
            if (request.getInputSource() == null || request.getInputSource().isBlank()) {
                return new UserComputeResult(false, "Input source cannot be null or empty");
            }
            if (request.getOutputDestination() == null || request.getOutputDestination().isBlank()) {
                return new UserComputeResult(false, "Output destination cannot be null or empty");
            }

            // Wrap the request
            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public List<Integer> getInputData() {
                    try {
                        return Files.lines(Paths.get(request.getInputSource()))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to read input file: " + e.getMessage(), e);
                    }
                }

                @Override
                public String getOutputDestination() {
                    return request.getOutputDestination();
                }
            };

            // Ask data storage to process request
            ProcessResult processResult = dataStore.processData(processRequest);
            if (!processResult.isSuccess()) {
                return new UserComputeResult(false, "Data storage failed: " + processResult.getMessage());
            }

            List<Integer> inputs = processRequest.getInputData();
            if (inputs == null || inputs.isEmpty()) {
                return new UserComputeResult(false, "No input numbers were provided");
            }

            // Run Collatz computation for each input number
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";
            String resultString = inputs.stream()
                    .map(n -> {
                        ComputeRequest computeRequest = () -> n;
                        ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);
                        if (!computeResult.isSuccess()) {
                            throw new RuntimeException("Computation failed for input: " + n);
                        }
                        return computeResult.getSequence();
                    })
                    .collect(Collectors.joining(delimiter));

            // Return clean result
            return new UserComputeResult(true, resultString);

        } catch (Exception e) {
            return new UserComputeResult(false, "Unexpected error: " + e.getMessage());
        }
    }
}
