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

        ProcessRequest request = new ProcessRequest() {
            @Override
            public List<Integer> getInputData() {
                return inputConfig.getInputNumbers();
            }

            @Override
            public String getOutputDestination() {
                return "in-memory";
            }
        };

        InMemoryDataStorage storage = new InMemoryDataStorage(inputConfig, outputConfig);
        ProcessResult result = storage.processData(request);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertFalse(outputResults.isEmpty());
    }
}
