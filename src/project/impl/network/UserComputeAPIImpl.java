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
            if (request.getInputSource() == null || request.getInputSource().trim().isEmpty()) {
                return new UserComputeResult(false, "Input source must be provided");
            }

            // Use user-defined or default delimiter
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            // Build ProcessRequest (no file reading here — handled by DataStorageComputeAPI)
            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public List<Integer> getInputData() {
                    return null; // handled internally by process layer
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
                    return null; // computed inside process layer
                }

                @Override
                public String getInputSource() {
                    return request.getInputSource();
                }
            };

            // Let the process API handle reading, computing, and writing
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
