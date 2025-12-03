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

import java.util.List;

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
            String outputPath = request.getOutputDestination();
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            // --- FILE READING MOVED TO DATA STORAGE API ---
            List<String> inputLines = dataStore.readInputFile(inputPath);

            StringBuilder finalOutput = new StringBuilder();

            for (String line : inputLines) {
                ComputeRequest computeRequest = new ComputeRequest() {
                    @Override
                    public int getInputNumber() {
                        try {
                            return Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            return -1;
                        }
                    }

                    @Override
                    public String getInputString() {
                        try {
                            Integer.parseInt(line);
                            return null;
                        } catch (NumberFormatException e) {
                            return line;
                        }
                    }
                };

                ComputeResult result = computeEngine.computeCollatz(computeRequest);
                if (!result.isSuccess()) {
                    return new UserComputeResult(false, "Computation failed for input: " + line);
                }

                finalOutput.append(result.getSequence().replace(",", delimiter)).append(System.lineSeparator());
            }

            // Write results via DataStorageComputeAPI
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
            }

            return new UserComputeResult(true, "Computation and storage completed successfully");

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
