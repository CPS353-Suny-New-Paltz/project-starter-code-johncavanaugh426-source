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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Multi-threaded implementation of UserComputeAPI that delegates to the
 // DataStorageComputeAPI concurrently. Thread pool is fixed at THREAD_LIMIT.
public class UserComputeAPIMultiThreaded implements UserComputeAPI {

    private static final int THREAD_LIMIT = 5;

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
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
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
                public List<Integer> getInputData() {
                    return null; // handled by process layer
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

            // Submit THREAD_LIMIT concurrent tasks that each call the dataStore.processData
            List<Future<ProcessResult>> futures = new ArrayList<>();
            for (int i = 0; i < THREAD_LIMIT; i++) {
                futures.add(executor.submit(() -> dataStore.processData(processRequest)));
            }

            // Wait for all tasks to complete and check results
            for (Future<ProcessResult> future : futures) {
                ProcessResult pr = future.get();
                if (pr == null || !pr.isSuccess()) {
                    String msg = pr == null ? "null ProcessResult" : pr.getMessage();
                    return new UserComputeResult(false, "Thread failed: " + msg);
                }
            }

            return new UserComputeResult(true, "All threads completed successfully");

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
