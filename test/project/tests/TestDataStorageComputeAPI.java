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
            @Override public List<Integer> getInputData() { 
                return Arrays.asList(1, 2, 3); 
            }
            @Override public String getOutputDestination() { 
                return null;
            }
        };

        ProcessResult result = realDataStore.processData(request);

        // Verify the method succeeded and returned a non-empty message
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess(), "Data store should succeed for simple input");
        Assertions.assertNotNull(result.getMessage());
        Assertions.assertFalse(result.getMessage().isEmpty());
    }
}
