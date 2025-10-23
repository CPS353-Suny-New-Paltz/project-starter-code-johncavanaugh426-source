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
        String delimiter = ";"; // changed to String
        String inputPath = "test" + File.separator + "testInputFile.test";

        // TODO 4: call coordinator to run compute job
        UserComputeRequest request = new MyUserComputeRequest(inputPath, outputPath, delimiter);

        UserComputeResult result = coordinator.processInput(request);

        if (!result.isSuccess()) {
            System.err.println("Computation failed: " + result.getMessage());
        } else {
            System.out.println("Computation completed: output written to " + outputPath);
        }
    }

    
    private static class MyUserComputeRequest implements UserComputeRequest {

        private final String inputSource;
        private final String outputDestination;
        private final String outputDelimiter;

        public MyUserComputeRequest(String inputSource, String outputDestination, String outputDelimiter) {
            this.inputSource = inputSource;
            this.outputDestination = outputDestination;
            this.outputDelimiter = outputDelimiter;
        }

        @Override
        public String getInputSource() {
            return inputSource;
        }

        @Override
        public String getOutputDestination() {
            return outputDestination;
        }

        @Override
        public String getOutputDelimiter() {
            return outputDelimiter;
        }
    }
}
