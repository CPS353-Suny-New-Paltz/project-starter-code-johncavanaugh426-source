package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

import java.io.IOException;
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
            // Step 1: Read input integers
            List<Integer> inputNumbers = request.getInputData();
            if (inputNumbers == null || inputNumbers.isEmpty()) {
                return new ProcessResult(false, "Input numbers are empty");
            }

            // Step 2: Convert numbers to a comma-separated string
            String output = inputNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // Step 3: Write output to user-specified destination
            Files.write(Paths.get(request.getOutputDestination()), output.getBytes());

            return new ProcessResult(true, "Data successfully written");
        } catch (IOException e) {
            return new ProcessResult(false, "Error writing to file: " + e.getMessage());
        } catch (Exception e) {
            return new ProcessResult(false, "Unexpected error: " + e.getMessage());
        }
    }
}
