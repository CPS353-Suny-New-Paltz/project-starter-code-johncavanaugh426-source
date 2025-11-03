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

// Multi-threaded version that delegates one computation job to the data store,
// which internally handles parallel processing across threads.
public class UserComputeAPIMultiThreaded implements UserComputeAPI {

    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;

    public UserComputeAPIMultiThreaded() {
        this(new DataStorageComputeAPIImpl(), new ComputeEngineAPIImpl());
    }

    public UserComputeAPIMultiThreaded(DataStorageComputeAPI dataStore, ComputeEngineAPI computeEngine) {
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

            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            // Build ProcessRequest that the DataStorage layer expects
            ProcessRequest processRequest = new ProcessRequest() {
                @Override
                public java.util.List<Integer> getInputData() {
                    return null; // handled by data store
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
                    return null;
                }

                @Override
                public String getInputSource() {
                    return request.getInputSource();
                }
            };

            // Call data store ONCE — it handles multi-threading internally
            ProcessResult processResult = dataStore.processData(processRequest);

            if (processResult == null || !processResult.isSuccess()) {
                String msg = processResult == null ? "null ProcessResult" : processResult.getMessage();
                return new UserComputeResult(false, "Data processing failed: " + msg);
            }

            return new UserComputeResult(true, "Multi-threaded computation completed successfully");

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
