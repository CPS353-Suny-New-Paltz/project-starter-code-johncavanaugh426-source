package project.api.conceptual;

public interface ComputeRequest {
    int getInputNumber();

    // Optional method for arbitrarily large inputs
    default String getInputString() {
        return null;
    }
}
