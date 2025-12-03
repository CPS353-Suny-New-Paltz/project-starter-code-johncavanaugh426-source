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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserComputeAPIMultiThreaded implements UserComputeAPI {

    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;
    private static final int THREAD_LIMIT = 8;

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

            String inputPath = request.getInputSource();
            String outputPath = request.getOutputDestination();
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            // --- FILE READING MOVED TO DATA STORAGE API ---
            List<String> inputLines = dataStore.readInputFile(inputPath);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
            List<Future<String>> futures = new ArrayList<>();

            for (String line : inputLines) {
                futures.add(executor.submit(() -> {
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
                        throw new RuntimeException("Computation failed for input: " + line);
                    }
                    return result.getSequence().replace(",", delimiter);
                }));
            }

            StringBuilder finalOutput = new StringBuilder();
            for (Future<String> f : futures) {
                finalOutput.append(f.get()).append(System.lineSeparator());
            }

            executor.shutdown();

            // Write results via DataStorageComputeAPI
            ProcessRequest writeRequest = new ProcessRequest() {
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

            ProcessResult writeResult = dataStore.processData(writeRequest);
            if (!writeResult.isSuccess()) {
                return new UserComputeResult(false, "Write failed: " + writeResult.getMessage());
            }

            return new UserComputeResult(true, "Multi-threaded computation completed successfully");

        } catch (Exception e) {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
