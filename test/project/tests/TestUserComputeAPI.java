package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;

public class TestUserComputeAPI {

    @Test
    public void smokeTestUserComputeAPI() throws Exception {
        // Write a simple input file the implementation will read
        Files.write(Paths.get("collatzInput.txt"), Arrays.asList("5", "6", "7", "10"));

        // Use the real implementation (required by the checkpoint tests)
        UserComputeAPI realApi = new UserComputeAPIImpl();
        Assertions.assertNotNull(realApi);

        
        UserComputeRequest request = new UserComputeRequest() {
            @Override public String getInputSource() { return "collatzInput.txt"; }
            @Override public String getOutputDelimiter() { return "\n"; }
            @Override public String getOutputDestination() { return null; }
        };

        UserComputeResult result = realApi.processInput(request);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess(), "UserComputeAPI should report success");
        Assertions.assertTrue(result.getMessage().contains("Input: 5"), "Should include Collatz sequence for 5");
        Assertions.assertTrue(result.getMessage().contains("Input: 10"), "Should include Collatz sequence for 10");
    }
}
