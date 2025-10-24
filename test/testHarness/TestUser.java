package testHarness;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;

import java.io.File;

public class TestUser {

    // TODO 3 fixed: use the @NetworkAPI interface
    private final UserComputeAPI coordinator;

    public TestUser(UserComputeAPI coordinator) {
        this.coordinator = coordinator;
    }
 
    public void run(String outputPath) {
        String delimiter = ";"; 
        String inputPath = "test" + File.separator + "testInputFile.test";

        // TODO 4: call coordinator to run compute job
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return inputPath;
            }

            @Override
            public String getOutputDestination() {
                return outputPath;
            }

            @Override
            public String getOutputDelimiter() { 
                return delimiter;
            }
        };

        UserComputeResult result = coordinator.processInput(request);

        if (!result.isSuccess()) {
            System.err.println("Computation failed: " + result.getMessage());
        } else {
            System.out.println("Computation completed: output written to " + outputPath);
        }
    }
}
