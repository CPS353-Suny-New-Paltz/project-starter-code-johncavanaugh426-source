package project.impl.network;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.api.process.DataStorageComputeAPI;
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
            if (request == null) {
                return new UserComputeResult(false, "Request cannot be null");
            }

            String inputPath = request.getInputSource();
            if (inputPath == null || inputPath.trim().isEmpty()) {
                return new UserComputeResult(false, "Input source must be provided");
            }

            if (!Files.exists(Paths.get(inputPath))) {
                return new UserComputeResult(false, "Input file does not exist: " + inputPath);
            }

            String outputPath = request.getOutputDestination();
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return new UserComputeResult(false, "Output destination must be provided");
            }

            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            List<Integer> inputData = Files.lines(Paths.get(inputPath))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

            if (inputData.isEmpty()) {
                return new UserComputeResult(false, "No input numbers provided");
            }

            StringBuilder finalOutput = new StringBuilder();
            for (int number : inputData) {
                ComputeRequest computeRequest = () -> number;
                ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                if (!computeResult.isSuccess()) {
                    return new UserComputeResult(false, "Computation failed for number: " + number);
                }

                finalOutput.append(computeResult.getSequence().replace(",", delimiter))
                    .append(System.lineSeparator());
            }

            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public List<Integer> getInputData() {
                    return null;
                }

                @Override
                public String getOutputDestination() {
                    return outputPath;
                }

                @Override
                public String getDelimiter() {
                    return delimiter;
                }

                @Override
                public String getComputedResults() {
                    return finalOutput.toString();
                }

                @Override
                public String getInputSource() {
                    return inputPath;
                }
            };

            ProcessResult processResult = dataStore.processData(processRequest);
            if (!processResult.isSuccess()) {
                return new UserComputeResult(false, "Data storage failed: " + processResult.getMessage());
            } else {
                return new UserComputeResult(true, "Computation and storage completed successfully");
            }

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
