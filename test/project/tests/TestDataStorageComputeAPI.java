
package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;

public class TestDataStorageComputeAPI {

    @Test
    public void smokeTestDataStorageComputeAPI() {
        
        DataStorageComputeAPI realDataStore = new DataStorageComputeAPIImpl();
        Assertions.assertNotNull(realDataStore);
        DataStorageComputeAPI mockDataStore = Mockito.mock(DataStorageComputeAPI.class);

        ProcessRequest mockRequest = new ProcessRequest() {
            @Override
            public java.util.List<Integer> getInputData() {
                return java.util.Arrays.asList(1, 2, 3);
            }

            @Override
            public String getOutputDestination() {
                return "output.txt";
            }
        };

        ProcessResult expectedResult = new ProcessResult(true, "mock processed");
        when(mockDataStore.processData(mockRequest)).thenReturn(expectedResult);

        ProcessResult result = mockDataStore.processData(mockRequest);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("mock processed", result.getMessage());

        verify(mockDataStore).processData(mockRequest);
    }
}
