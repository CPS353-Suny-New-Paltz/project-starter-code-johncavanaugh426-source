package project.testutil;

import java.util.List;
import java.util.stream.Collectors;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

public class InMemoryDataStorage implements DataStorageComputeAPI {

    private final InMemoryInputConfig inputConfig;
    private final InMemoryOutputConfig outputConfig;

    public InMemoryDataStorage(InMemoryInputConfig inputConfig, InMemoryOutputConfig outputConfig) {
        this.inputConfig = inputConfig;
        this.outputConfig = outputConfig;
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        List<Integer> inputNumbers = inputConfig.getInputNumbers();
        List<String> output = outputConfig.getOutputResults();

        for (Integer num : inputNumbers) {
            output.add(String.valueOf(num));
        }

        return new ProcessResult(true, "Processed " + inputNumbers.size() + " numbers.");
    }

    @Override
    public List<String> readInputFile(String inputPath) {
        // Instead of reading a file, return the in-memory numbers as strings
        List<Integer> inputNumbers = inputConfig.getInputNumbers();
        return inputNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }
}
