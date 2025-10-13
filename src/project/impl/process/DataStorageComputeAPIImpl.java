package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

    public DataStorageComputeAPIImpl() {
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        try {
            // Validation checks
            if (request == null) {
                return new ProcessResult(false, "Request cannot be null");
            }

            List<Integer> inputData = request.getInputData();
            if (inputData == null || inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return new ProcessResult(false, "Output destination cannot be null or empty");
            }

            // Get delimiter and computed results
            String delimiter = request.getDelimiter() != null ? request.getDelimiter() : ",";
            String results = request.getComputedResults();

            // Fallback: if computed results aren't provided, use input numbers
            if (results == null || results.isEmpty()) {
                results = inputData.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(delimiter));
            }

            results = results.replace("\r\n", "\n");
            List<String> cleanedLines = Arrays.stream(results.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();

            String cleanOutput = String.join("\n", cleanedLines) + "\n";
            Files.writeString(Paths.get(outputPath), cleanOutput);
            System.out.println("Computation Results: ");
            System.out.print(cleanOutput);

            return new ProcessResult(true, "Data processed successfully");

        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}
