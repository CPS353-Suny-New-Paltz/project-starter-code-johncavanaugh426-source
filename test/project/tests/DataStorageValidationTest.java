package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;

import java.util.Collections;
import java.util.List;

public class DataStorageValidationTest {

    // Test that validation fails if input list is empty
    @Test
    public void testEmptyListValidationFails() {
        DataStorageComputeAPIImpl storageApi = new DataStorageComputeAPIImpl();

        ProcessRequest emptyRequest = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return Collections.emptyList();
            }

            @Override
            public String getOutputDestination() {
                return null;
            }
        };

        ProcessResult result = storageApi.processData(emptyRequest);

        // Debug print
        System.out.println("Empty list test message: " + result.getMessage());

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("No input numbers provided"));
    }

    // Test that validation succeeds with valid data
    @Test
    public void testValidListPassesValidation() {
        DataStorageComputeAPIImpl storageApi = new DataStorageComputeAPIImpl();

        ProcessRequest validRequest = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return List.of(5, 10, 15);
            }

            @Override 
            public String getOutputDestination() {
                return null; // no output file
            }
        };

        ProcessResult result = storageApi.processData(validRequest);

        // Debug print
        System.out.println("Valid list test message: " + result.getMessage());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Data processed successfully", result.getMessage());
    }
}
