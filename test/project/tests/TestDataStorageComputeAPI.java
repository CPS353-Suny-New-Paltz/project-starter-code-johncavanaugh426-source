package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;

public class TestDataStorageComputeAPI {

    @Test
    public void smokeTestDataStorageComputeAPI() {
        DataStorageComputeAPI realDataStore = new DataStorageComputeAPIImpl();
        Assertions.assertNotNull(realDataStore);

        ProcessRequest request = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return Arrays.asList(1, 2, 3);
            }

            @Override
            public String getOutputDestination() {
                return "test_output.txt";
            }

            @Override
            public String getDelimiter() {
                return ",";
            }

            @Override
            public String getComputedResults() {
                // Pretend the computation results already exist
                return "1,4,2,1";
            }
        };

        ProcessResult result = realDataStore.processData(request);

        // Verify method behavior
        Assertions.assertNotNull(result, "ProcessResult should not be null");
        Assertions.assertTrue(result.isSuccess(), "Data store should succeed for simple input");
        Assertions.assertNotNull(result.getMessage(), "Result message should not be null");
        Assertions.assertFalse(result.getMessage().isEmpty(), "Result message should not be empty");
    }
}
