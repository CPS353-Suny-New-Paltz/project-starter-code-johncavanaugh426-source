package project.api.conceptual;

public class ComputeResult {
    private final boolean success;
    private final String sequence;

    public ComputeResult(boolean success, String sequence) {
        this.success = success;
        this.sequence = sequence;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSequence() {
        return sequence;
    }
}
