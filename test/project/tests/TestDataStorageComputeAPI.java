package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;

public class TestDataStorageComputeAPI {

    @Test
    public void smokeTestDataStorageComputeAPI() throws IOException {
        DataStorageComputeAPI realDataStore = new DataStorageComputeAPIImpl();
        Assertions.assertNotNull(realDataStore);

        // Create temporary input and output files
        Path inputFile = Files.createTempFile("smoke_input", ".txt");
        Path outputFile = Files.createTempFile("smoke_output", ".txt");

        // Write simple input numbers to the input file
        Files.writeString(inputFile, "1\n2\n3\n");

        ProcessRequest request = new ProcessRequest() {
            @Override
            public java.util.List<Integer> getInputData() { return null; }

            @Override
            public String getOutputDestination() { return outputFile.toString(); }

            @Override
            public String getDelimiter() { return ","; }

            @Override
            public String getComputedResults() {
                // Provide dummy Collatz sequences for 1, 2, 3
                return "1\n2,1\n3,10,5,16,8,4,2,1";
            }

            @Override
            public String getInputSource() { return inputFile.toString(); }
        };

        ProcessResult result = realDataStore.processData(request);

        // Verify method behavior
        Assertions.assertNotNull(result, "ProcessResult should not be null");
        Assertions.assertTrue(result.isSuccess(), "Data store should succeed for valid input file");
        Assertions.assertNotNull(result.getMessage(), "Result message should not be null");
        Assertions.assertFalse(result.getMessage().isEmpty(), "Result message should not be empty");

        // Optional: confirm that output file contains computed Collatz sequences
        String outputContent = Files.readString(outputFile);
        Assertions.assertFalse(outputContent.isBlank(), "Output file should contain computed results");

        System.out.println("Smoke test output:\n" + outputContent);
    }
}
