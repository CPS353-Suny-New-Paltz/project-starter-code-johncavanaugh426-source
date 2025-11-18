package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

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

            List<String> inputLines = Files.lines(Paths.get(inputPath))
                                           .map(String::trim)
                                           .filter(s -> !s.isEmpty())
                                           .collect(Collectors.toList());

            if (inputLines.isEmpty()) {
                return new ProcessResult(false, "Input file is empty");
            }

            String outputPath = request.getOutputDestination();
            if (outputPath == null || outputPath.trim().isEmpty()) {
                return new ProcessResult(false, "Output destination cannot be null or empty");
            }

            String computedResults = request.getComputedResults();
            if (computedResults == null || computedResults.isEmpty()) {
                return new ProcessResult(false, "No computed results provided");
            }

            // Write the computed results to file
            List<String> outputLines = List.of(computedResults.split("\\R"));
            Files.write(Paths.get(outputPath), outputLines);

            return new ProcessResult(true, "Data processed successfully");

        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}