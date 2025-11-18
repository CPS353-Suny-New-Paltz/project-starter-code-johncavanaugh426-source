package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataStorageValidationTest {

    @Test
    public void testEmptyFileValidationFails() throws IOException {
        DataStorageComputeAPIImpl storageApi = new DataStorageComputeAPIImpl();

        // Create an empty temporary input file
        Path inputFile = Files.createTempFile("empty_input", ".txt");
        Path outputFile = Files.createTempFile("output", ".txt");

        ProcessRequest emptyRequest = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() { 
            	return null; 
            	}

            @Override
            public String getOutputDestination() {
                return outputFile.toString();
            }

            @Override
            public String getDelimiter() { 
            	return ","; 
            	}

            @Override
            public String getComputedResults() {
                // Provide at least a placeholder to satisfy new API
                return "";
            }

            @Override
            public String getInputSource() {
                return inputFile.toString();
            }
        };

        ProcessResult result = storageApi.processData(emptyRequest);

        System.out.println("Empty file test message: " + result.getMessage());

        // Should still fail because file is empty
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Input file is empty"));
    }

    @Test
    public void testValidFilePassesValidation() throws IOException {
        DataStorageComputeAPIImpl storageApi = new DataStorageComputeAPIImpl();

        // Create temp input file with some numbers
        Path inputFile = Files.createTempFile("valid_input", ".txt");
        Path outputFile = Files.createTempFile("output", ".txt");
        Files.writeString(inputFile, "5\n10\n15\n");

        ProcessRequest validRequest = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() { 
            	return null; 
            	}

            @Override
            public String getOutputDestination() { 
            	return outputFile.toString(); 
            	}

            @Override
            public String getDelimiter() { 
            	return ","; 
            	}

            @Override
            public String getComputedResults() {
                // Provide some fake computed results so the storage layer can write it
                return "5,16,8,4,2,1\n10,5,16,8,4,2,1\n15,46,23,70,35,106,53,160,80,40,20,10,5,16,8,4,2,1";
            }

            @Override
            public String getInputSource() { 
            	return inputFile.toString(); 
            	}
        };

        ProcessResult result = storageApi.processData(validRequest);

        System.out.println("Valid file test message: " + result.getMessage());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Data processed successfully", result.getMessage());
    }
}
