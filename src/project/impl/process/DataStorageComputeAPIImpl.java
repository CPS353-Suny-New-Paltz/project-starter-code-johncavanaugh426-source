package project.impl.process;

import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;

import java.util.List;

public class DataStorageComputeAPIImpl implements DataStorageComputeAPI {

    public DataStorageComputeAPIImpl() {
       
    }

    @Override
    public ProcessResult processData(ProcessRequest request) {
        try {
            // Step 1: Get input numbers from request
            List<Integer> inputNumbers = request.getInputData();
            if (inputNumbers == null || inputNumbers.isEmpty()) {
                return new ProcessResult(false, "No input numbers provided");
            }


            return new ProcessResult(true, "Processed " + inputNumbers.size() + " numbers");
        } catch (Exception e) {
            return new ProcessResult(false, "Error processing data: " + e.getMessage());
        }
    }
}
