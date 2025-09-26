package project.api.network;

public interface UserComputeRequest {
    String getInputSource();        // input
    String getOutputDelimiter();    // if null
    String getOutputDestination();  // Output
    
}
