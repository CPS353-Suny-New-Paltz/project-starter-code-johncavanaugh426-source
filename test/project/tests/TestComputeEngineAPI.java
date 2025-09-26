package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.ComputeEngineAPIImpl;

public class TestComputeEngineAPI {

    @Test
    public void smokeTestComputeEngine() {
        ComputeEngineAPI realEngine = new ComputeEngineAPIImpl();
        Assertions.assertNotNull(realEngine);
        ComputeRequest request = new ComputeRequest() {
            @Override public int getInputNumber() { 
            	return 6; 
            	}
        };

        ComputeResult result = realEngine.computeCollatz(request);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess(), "ComputeEngine should succeed for input 6");
        String sequence = result.getSequence();
        Assertions.assertTrue(sequence.startsWith("6"), "Sequence should start with input number");

        // Split the sequence into numbers and check the last one is 1
        String[] parts = sequence.split(",");
        String last = parts[parts.length - 1].trim();
        Assertions.assertEquals("1", last, "Collatz sequence should end with 1");
    }
}
