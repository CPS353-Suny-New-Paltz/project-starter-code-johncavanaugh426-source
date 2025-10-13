package project.prototype;

import project.annotations.ProcessAPIPrototype;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import java.util.Arrays;
import java.util.List;

public class DataStorageComputeAPIPrototype {

    @ProcessAPIPrototype
    public ProcessResult prototypeProcessData(DataStorageComputeAPI api) {
        // Mock request for demonstration purposes
        ProcessRequest mockRequest = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return Arrays.asList(6);
            }

            @Override
            public String getOutputDestination() {
                return "output_data.txt";
            }

            @Override
            public String getDelimiter() {
                return ","; // default for testing
            }

            @Override
            public String getComputedResults() {
                return "Input: 6 -> Collatz Sequence: 6,3,10,5,16,8,4,2,1";
            }
        };

        System.out.println("Mock processing input: " + mockRequest.getInputData());
        System.out.println("Writing output to: " + mockRequest.getOutputDestination());
        System.out.println("Using delimiter: " + mockRequest.getDelimiter());
        System.out.println("Mock computed results: " + mockRequest.getComputedResults());

        // Call the real API with mock data
        ProcessResult result = api.processData(mockRequest);

        System.out.println("API returned: " + result.getMessage());
        return result;
    }
}
