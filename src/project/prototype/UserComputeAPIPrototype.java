package project.prototype;

import project.annotations.NetworkAPIPrototype;
import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;

public class UserComputeAPIPrototype {

    @NetworkAPIPrototype
    public UserComputeResult prototypeProcessInput(UserComputeAPI api) {
        UserComputeRequest mockRequest = new UserComputeRequest() {
            public String getInputSource() {
            	return "input.txt";
            	}
            public String getOutputDelimiter() {
            	return null; 
            	} 
            public String getOutputDestination() { 
            	return "output.txt";
            	}
        };

        String delimiter = mockRequest.getOutputDelimiter();
        if (delimiter == null || delimiter.isEmpty()) {
            delimiter = ","; 
        }

        System.out.println("Reading input from: " + mockRequest.getInputSource());
        System.out.println("Writing output to: " + mockRequest.getOutputDestination());
        System.out.println("Using output delimiter: " + delimiter);
        return new UserComputeResult(true, "Prototype complete");
    }
}
