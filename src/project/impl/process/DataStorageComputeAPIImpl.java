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
            // Read the input data (if file path provided)
            List<Integer> inputData = request.getInputData();
            if (inputData == null || inputData.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }

            // If output destination is specified, write results to file
            String outputPath = request.getOutputDestination();
            if (outputPath != null) {
                Files.write(Paths.get(outputPath),
                        inputData.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.toList()));
            }

            return new ProcessResult(true, "Data processed successfully");
        } catch (Exception e) {
            return new ProcessResult(false, e.getMessage());
        }
    }
}
