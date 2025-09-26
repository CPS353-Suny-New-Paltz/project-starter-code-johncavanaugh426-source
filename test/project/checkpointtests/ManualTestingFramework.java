package project.checkpointtests;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;
import project.impl.process.DataStorageComputeAPIImpl;
import project.impl.conceptual.ComputeEngineAPIImpl;

public class ManualTestingFramework {
    
    public static final String INPUT = "manualTestInput.txt";
    public static final String OUTPUT = "manualTestOutput.txt";

    public static void main(String[] args) {
        // TODO 1: Instantiate the real APIs
        UserComputeAPI userAPI = new UserComputeAPIImpl(
            new DataStorageComputeAPIImpl(),
            new ComputeEngineAPIImpl()
        );

        // TODO 2: Run computation with input/output files
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return INPUT; 
            }

            @Override
            public String getOutputDelimiter() {
                return ","; 
            }

            @Override
            public String getOutputDestination() {
                return OUTPUT; // writes to manualTestOutput.txt
            }
        };

        UserComputeResult result = userAPI.processInput(request);

        // Print to console so we can verify it worked
        if (result.isSuccess()) {
            System.out.println("Computation succeeded!");
            System.out.println("Result: " + result.getMessage());
            System.out.println("Output written to: " + OUTPUT);
        } else {
            System.out.println("Computation failed: " + result.getMessage());
        }
    }
}
