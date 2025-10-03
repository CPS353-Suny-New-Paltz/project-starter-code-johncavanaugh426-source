package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

    public DataStorageComputeAPIImpl() {
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        try {
            // Validation
            if (request == null) {
                return new ProcessResult(false, "ProcessRequest cannot be null");
            }

            List<Integer> inputData = request.getInputData();
            if (inputData == null || inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath != null && !outputPath.isBlank()) {
                try {
                    // Join all numbers into one comma-separated line
                    String singleLineOutput = inputData.stream()
                                                       .map(String::valueOf)
                                                       .collect(Collectors.joining(","));
                    Files.writeString(Paths.get(outputPath), singleLineOutput);
                } catch (Exception fileEx) {
                    // Catch file I/O separately so it does not propagate
                    return new ProcessResult(false, "Failed to write to output file: " + fileEx.getMessage());
                }
            }

            return new ProcessResult(true, "Data processed successfully");
        } catch (Exception e) {
            // Catch
            return new ProcessResult(false, "Unexpected error: " + e.getMessage());
        }
    }
}
