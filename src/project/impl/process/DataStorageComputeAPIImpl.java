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
            // Validation checks
            if (request == null) {
                return new ProcessResult(false, "Request cannot be null");
            }

            List<Integer> inputData = request.getInputData();
            if (inputData == null || inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath != null) {
                if (outputPath.trim().isEmpty()) {
                    return new ProcessResult(false, "Output destination cannot be empty string");
                }
                // Join numbers into one line
                String singleLineOutput = inputData.stream()
                                                   .map(String::valueOf)
                                                   .collect(Collectors.joining(","));
                // Write output
                Files.writeString(Paths.get(outputPath), singleLineOutput);
            }

            return new ProcessResult(true, "Data processed successfully");
        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}
