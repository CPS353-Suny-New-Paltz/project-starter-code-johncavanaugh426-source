package testharness;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;

public class TestUser
{

    private final UserComputeAPI coordinator;

    public TestUser(UserComputeAPI coordinator)
    {
        this.coordinator = coordinator;
    }

    // Run method with both output and input paths
    public void run(String outputPath, String inputPath)
    {
        String delimiter = ";";

        UserComputeRequest request = new UserComputeRequest()
        {
            @Override
            public String getInputSource()
            {
                return inputPath;
            }

            @Override
            public String getOutputDestination()
            {
                return outputPath;
            }

            @Override
            public String getOutputDelimiter()
            {
                return delimiter;
            }
        };

        UserComputeResult result = coordinator.processInput(request);

        if (!result.isSuccess())
        {
            System.err.println("Computation failed: " + result.getMessage());
        }
        else
        {
            System.out.println("Computation completed: output written to " + outputPath);
        }
    }
}
