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
            // Validation checks
            if (request == null) {
                return new UserComputeResult(false, "Request cannot be null");
            }
            if (request.getInputSource() == null || request.getInputSource().trim().isEmpty()) {
                return new UserComputeResult(false, "Input source must be provided");
            }
            if (!Files.exists(Paths.get(request.getInputSource()))) {
                return new UserComputeResult(false, "Input file does not exist: " + request.getInputSource());
            }

            // Read input numbers
            List<Integer> inputs = Files.lines(Paths.get(request.getInputSource()))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (inputs.isEmpty()) {
                return new UserComputeResult(false, "No input numbers were provided");
            }

            // Compute results
            StringBuilder resultsBuilder = new StringBuilder();
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            for (int number : inputs) {
                ComputeRequest computeRequest = () -> number;
                ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                if (!computeResult.isSuccess()) {
                    return new UserComputeResult(false, "Computation failed for input: " + number);
                }

                // Replace commas in the sequence with user delimiter
                String sequence = computeResult.getSequence().replace(",", delimiter);

                // Each sequence on its own line
                resultsBuilder.append(sequence).append(System.lineSeparator());
            }

            // Build computed results string
            String computedResults = resultsBuilder.toString();

            // Wrap everything into a ProcessRequest for storage
            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public List<Integer> getInputData() {
                    return inputs;
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
                    return computedResults;
                }
            };

            // Ask data storage to write results
            ProcessResult processResult = dataStore.processData(processRequest);
            if (!processResult.isSuccess()) {
                return new UserComputeResult(false, "Data storage failed: " + processResult.getMessage());
            }

            return new UserComputeResult(true, "Computation and storage completed successfully");

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
