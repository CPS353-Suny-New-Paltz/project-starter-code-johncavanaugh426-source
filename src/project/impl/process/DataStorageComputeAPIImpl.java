package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.ComputeEngineAPIImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

    private static final int THREAD_LIMIT = 5;
    private final ComputeEngineAPI computeEngine = new ComputeEngineAPIImpl();

    @Override
    public ProcessResult processData(ProcessRequest request) {
        try {
            if (request == null) {
                return new ProcessResult(false, "Request cannot be null");
            }

            String inputPath = request.getInputSource();
            if (inputPath == null || inputPath.trim().isEmpty()) {
                return new ProcessResult(false, "Input source cannot be null or empty");
            }

            if (!Files.exists(Paths.get(inputPath))) {
                return new ProcessResult(false, "Input file does not exist: " + inputPath);
            }

            List<Integer> inputData = Files.lines(Paths.get(inputPath))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

            if (inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return new ProcessResult(false, "Output destination cannot be null or empty");
            }

            String delimiter = request.getDelimiter() != null ? request.getDelimiter() : ",";

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
            List<Future<String>> results = new ArrayList<>();

            for (int number : inputData) {
                results.add(executor.submit(() -> {
                    ComputeRequest computeRequest = () -> number;
                    ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                    if (!computeResult.isSuccess()) {
                        throw new RuntimeException("Computation failed for " + number);
                    }

                    return computeResult.getSequence().replace(",", delimiter);
                }));
            }

            List<String> outputLines = new ArrayList<>();
            for (Future<String> f : results) {
                outputLines.add(f.get());
            }

            executor.shutdown();

            Files.write(Paths.get(outputPath), outputLines);

            return new ProcessResult(true, "Data processed successfully");
        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}
