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
            // Step 1: Wrap the request into a ProcessRequest
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

            // Ask data storage to process the request
            ProcessResult processResult = dataStore.processData(processRequest);
            if (!processResult.isSuccess()) {
                return new UserComputeResult(false, "Data storage failed: " + processResult.getMessage());
            }

            List<Integer> inputs = processRequest.getInputData();
            if (inputs == null || inputs.isEmpty()) {
                return new UserComputeResult(false, "No input numbers were provided");
            }

            // Step 2: Run Collatz computation for each input number
            StringBuilder resultsBuilder = new StringBuilder();
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";
            for (int i = 0; i < inputs.size(); i++) {
                int number = inputs.get(i);

                ComputeRequest computeRequest = () -> number;
                ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                if (!computeResult.isSuccess()) {
                    return new UserComputeResult(false, "Computation failed for input: " + number);
                }

                resultsBuilder.append("Input: ")
                        .append(number)
                        .append(" -> Collatz Sequence: ")
                        .append(computeResult.getSequence());

                if (i < inputs.size() - 1) {
                    resultsBuilder.append(delimiter);
                }
            }

            // Step 3: Return result to user
            return new UserComputeResult(true, resultsBuilder.toString());

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
