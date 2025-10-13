package project.tests;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.testutil.InMemoryInputConfig;
import project.testutil.InMemoryOutputConfig;
import project.testutil.InMemoryDataStorage;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.api.conceptual.ComputeEngineAPI;
import project.impl.conceptual.ComputeEngineAPIImpl;
import project.api.network.UserComputeAPI;
import project.impl.network.UserComputeAPIImpl;

public class ComputeEngineIntegrationTest {

    @Test
    public void testIntegrationWithInMemoryConfigs() {
        // Instantiate real APIs for checkpoint checks
        ComputeEngineAPI conceptualEngine = new ComputeEngineAPIImpl();
        UserComputeAPI networkEngine = new UserComputeAPIImpl();
        Assertions.assertNotNull(conceptualEngine);
        Assertions.assertNotNull(networkEngine);

        List<Integer> inputNumbers = new ArrayList<>();
        inputNumbers.add(1);
        inputNumbers.add(10);
        inputNumbers.add(25);

        List<String> outputResults = new ArrayList<>();
        InMemoryInputConfig inputConfig = new InMemoryInputConfig(inputNumbers);
        InMemoryOutputConfig outputConfig = new InMemoryOutputConfig(outputResults);

        // Updated ProcessRequest implementation
        ProcessRequest request = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return inputConfig.getInputNumbers();
            }

            @Override
            public String getOutputDestination() {
                return "in-memory";
            }

            @Override
            public String getDelimiter() {
                return ","; // default delimiter for test
            }

            @Override
            public String getComputedResults() {
                // simple mock of expected computed results
                return "1\n10,5,16,8,4,2,1\n25,76,38,19,58,29,88,44,22,11,34,17,52,26,13,40,20,10,5,16,8,4,2,1";
            }
        };

        InMemoryDataStorage storage = new InMemoryDataStorage(inputConfig, outputConfig);
        ProcessResult result = storage.processData(request);

        Assertions.assertTrue(result.isSuccess(), "Process should complete successfully");
        Assertions.assertFalse(outputResults.isEmpty(), "Output results should not be empty");
    }
}
