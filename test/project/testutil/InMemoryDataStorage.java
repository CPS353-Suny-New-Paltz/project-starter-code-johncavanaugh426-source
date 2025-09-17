package project.testutil;
import java.util.List;
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
        List<Integer> inputnumbers = inputConfig.getInputNumbers();
        List<String> output = outputConfig.getOutputResults();

        for (Integer num : inputnumbers) {
            output.add(String.valueOf(num));
        }
        return new ProcessResult(true, "Processed " + inputnumbers.size() + " numbers.");
    }

    }
