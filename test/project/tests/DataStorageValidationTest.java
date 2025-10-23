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
                return null; // no longer used
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
                return "";
            }

            @Override
            public String getInputSource() {
                return inputFile.toString();
            }
        };

        ProcessResult result = storageApi.processData(emptyRequest);

        System.out.println("Empty file test message: " + result.getMessage());

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("No input numbers provided"));
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
                return null; // handled by file reading now
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
                return null;
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
