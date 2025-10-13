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
import java.util.List;
import java.util.stream.Collectors;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

    private final ComputeEngineAPI computeEngine;

    public DataStorageComputeAPIImpl() {
        this.computeEngine = new ComputeEngineAPIImpl();
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        try {
            if (request == null) {
                return new ProcessResult(false, "Request cannot be null");
            }

            List<Integer> inputData = request.getInputData();
            if (inputData == null || inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return new ProcessResult(false, "Output destination cannot be empty");
            }

            // Use user-specified delimiter, default to comma
            String delimiter = request.getDelimiter() != null ? request.getDelimiter() : ",";

            // Compute sequences for each number
            String output = inputData.stream()
                    .map(number -> {
                        ComputeRequest computeRequest = () -> number;
                        ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);
                        if (!computeResult.isSuccess()) return "";
                        return computeResult.getSequence()
                                .lines()
                                .collect(Collectors.joining(delimiter));
                    })
                    .collect(Collectors.joining("\n")); // separate sequences by newline

            Files.writeString(Paths.get(outputPath), output);

            return new ProcessResult(true, "Data processed successfully");

        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}
