package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;

public class TestUserComputeAPI {

    @Test
    public void smokeTestUserComputeAPI() {
        // Real object for checkpoint verification
        UserComputeAPI realApi = new UserComputeAPIImpl();
        Assertions.assertNotNull(realApi);

        // Mock object for smoke testing
        UserComputeAPI mockAPI = Mockito.mock(UserComputeAPI.class);

        UserComputeRequest mockRequest = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "input_data.txt";
            }

            @Override
            public String getOutputDelimiter() {
                return null;
            }

            @Override
            public String getOutputDestination() {
                return "output_data.txt";
            }
        };

        UserComputeResult expectedResult = new UserComputeResult(true, "mock success");
        when(mockAPI.processInput(mockRequest)).thenReturn(expectedResult);

        UserComputeResult result = mockAPI.processInput(mockRequest);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("mock success", result.getMessage());

        verify(mockAPI).processInput(mockRequest);
    }
}
