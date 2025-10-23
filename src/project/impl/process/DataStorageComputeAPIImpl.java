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

            // Read input numbers directly from file
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
            StringBuilder resultBuilder = new StringBuilder();

            // Compute Collatz sequences for each number
            for (int number : inputData) {
                ComputeRequest computeRequest = () -> number;
                ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                if (!computeResult.isSuccess()) {
                    return new ProcessResult(false, "Computation failed for input: " + number);
                }

                // Replace commas with user’s chosen delimiter
                String sequence = computeResult.getSequence().replace(",", delimiter);
                resultBuilder.append(sequence).append(System.lineSeparator());
            }

            // Write results to file
            String finalOutput = resultBuilder.toString().trim() + System.lineSeparator();
            Files.writeString(Paths.get(outputPath), finalOutput);

            // Also print to console for visibility
            System.out.println("Computation Results:");
            System.out.print(finalOutput);

            return new ProcessResult(true, "Data processed successfully");

        } catch (Exception e) {
            return new ProcessResult(false, "Data storage error: " + e.getMessage());
        }
    }
}
