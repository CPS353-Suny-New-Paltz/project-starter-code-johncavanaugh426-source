package project.api.network;

public class UserComputeResult {
    private final boolean success;
    private final String message;

    public UserComputeResult(boolean success, String message) {
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
