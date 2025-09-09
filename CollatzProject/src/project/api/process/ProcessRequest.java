package project.api.process;

import java.util.List;

public interface ProcessRequest {
    List<Integer> getInputData();        
    String getOutputDestination();       
}
