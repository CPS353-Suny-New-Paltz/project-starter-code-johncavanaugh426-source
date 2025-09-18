package project.tests;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

public class TestComputeEngineAPI {

    @Test
    public void smokeTestComputeEngine() {
        ComputeEngineAPI mockEngine = mock(ComputeEngineAPI.class);
        ComputeRequest mockRequest = new ComputeRequest() {
            @Override
            public int getInputNumber() {
                return 6;
            }
        };

        ComputeResult expectedResult = new ComputeResult(true, "mock result");
        when(mockEngine.computeCollatz(mockRequest)).thenReturn(expectedResult);
        ComputeResult result = mockEngine.computeCollatz(mockRequest);
        assert(result.isSuccess());
        assert(result.getSequence().equals("mock result"));
        verify(mockEngine).computeCollatz(mockRequest);
    }
}
