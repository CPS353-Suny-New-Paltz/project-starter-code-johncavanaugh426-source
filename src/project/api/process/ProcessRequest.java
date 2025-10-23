package project.api.process;

import java.util.List;

public interface ProcessRequest {
    List<Integer> getInputData();        
    String getOutputDestination();       
    String getDelimiter();               
    String getComputedResults();         

    // new method to move file reading into the process layer
    String getInputSource();
}
