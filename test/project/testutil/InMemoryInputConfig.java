package project.testutil;

import java.util.List;

public class InMemoryInputConfig {
    private final List<Integer> inputNumbers;
    
    public InMemoryInputConfig(List<Integer> inputNumbers) {
        this.inputNumbers = inputNumbers;
    }
    public List<Integer> getInputNumbers() {
        return inputNumbers;
    }
}

