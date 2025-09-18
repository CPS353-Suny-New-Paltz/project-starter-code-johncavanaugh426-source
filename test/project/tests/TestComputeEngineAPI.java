package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.ComputeEngineAPIImpl;

public class TestComputeEngineAPI {

    @Test
    public void smokeTestComputeEngine() {
       
        ComputeEngineAPI realEngine = new ComputeEngineAPIImpl();
        Assertions.assertNotNull(realEngine);

    
        ComputeEngineAPI mockEngine = Mockito.mock(ComputeEngineAPI.class);

        ComputeRequest mockRequest = new ComputeRequest() {
            @Override
            public int getInputNumber() {
                return 6;
            }
        };

        ComputeResult expectedResult = new ComputeResult(true, "mock result");
        when(mockEngine.computeCollatz(mockRequest)).thenReturn(expectedResult);

        ComputeResult result = mockEngine.computeCollatz(mockRequest);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("mock result", result.getSequence());

        verify(mockEngine).computeCollatz(mockRequest);
    }
}