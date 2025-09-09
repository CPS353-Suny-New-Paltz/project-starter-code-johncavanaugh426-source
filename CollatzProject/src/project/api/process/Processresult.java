package project.api.process;

public class Processresult {
    private final boolean success;
    private final String message;

    public Processresult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
