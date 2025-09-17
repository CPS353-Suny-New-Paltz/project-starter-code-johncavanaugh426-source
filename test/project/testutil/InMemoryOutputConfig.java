package project.testutil;
import java.util.List;

public class InMemoryOutputConfig {
    private final List<String> outputResults;
    public InMemoryOutputConfig(List<String> outputResults) {
        this.outputResults = outputResults;
    }

    public List<String> getOutputResults() {
        return outputResults;
    }

    public void writeResult(String result) {
        outputResults.add(result);
    }
}
