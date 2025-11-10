package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserComputeIntegrationTest {

    // Test: missing input file
    @Test
    public void testMissingFileReturnsError() throws Exception {
        Path inputPath = Paths.get("nonexistent_input.txt");
        Files.deleteIfExists(inputPath); // ensure file is missing

        UserComputeAPIImpl api = new UserComputeAPIImpl();
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "nonexistent_input.txt";
            }

            @Override
            public String getOutputDestination() {
                return "output.txt"; // provide a dummy output to satisfy API
            }

            @Override
            public String getOutputDelimiter() {
                return null;
            }
        };

        UserComputeResult result = api.processInput(request);

        System.out.println("Missing file test message: " + result.getMessage());

        // Should fail because input file doesn't exist
        Assertions.assertFalse(result.isSuccess(), "Should fail for missing input file");
        Assertions.assertTrue(
            result.getMessage().toLowerCase().contains("does not exist") ||
            result.getMessage().toLowerCase().contains("cannot find"),
            "Message should indicate missing file"
        );
    }

    // Test: empty input file
    @Test
    public void testEmptyInputFileReturnsError() throws Exception {
        Path inputPath = Paths.get("empty_input.txt");
        Files.writeString(inputPath, ""); // create empty file

        UserComputeAPIImpl api = new UserComputeAPIImpl();
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "empty_input.txt";
            }

            @Override
            public String getOutputDestination() {
                return "output.txt"; // provide dummy output
            }

            @Override
            public String getOutputDelimiter() {
                return null;
            }
        };

        UserComputeResult result = api.processInput(request);

        System.out.println("Empty file test message: " + result.getMessage());

        // Should fail because no valid data is present
        Assertions.assertFalse(result.isSuccess(), "Should fail for empty input file");
        Assertions.assertTrue(
            result.getMessage().toLowerCase().contains("no input") ||
            result.getMessage().toLowerCase().contains("empty"),
            "Message should indicate the file had no valid numbers"
        );

        Files.deleteIfExists(inputPath);
    }
}
