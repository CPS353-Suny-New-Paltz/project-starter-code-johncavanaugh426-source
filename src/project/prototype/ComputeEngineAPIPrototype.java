package project.prototype;

import project.annotations.ConceptualAPIPrototype;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

public class ComputeEngineAPIPrototype {

    @ConceptualAPIPrototype
    public ComputeResult prototypeComputeCollatz(ComputeEngineAPI api) {
        // Mock request
        ComputeRequest mockRequest = new ComputeRequest() {
            public int getInputNumber() { 
            	return 6; 
            	}
        };

        System.out.println("Computing Collatz sequence for: " + mockRequest.getInputNumber());
        return new ComputeResult(true, "6 -> 3 -> 10 -> 5 -> 16 -> 8 -> 4 -> 2 -> 1");
    }
}
