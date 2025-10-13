package project.tests;

import org.junit.jupiter.api.Test;
import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;
import project.impl.process.DataStorageComputeAPIImpl;
import project.impl.conceptual.ComputeEngineAPIImpl;

import java.io.FileWriter;
import java.io.IOException;

public class CollatzTest {

    @Test
    public void testCollatzSequences() {
        // Write the test numbers to collatzInput.txt
        try (FileWriter writer = new FileWriter("collatzInput.txt")) {
            writer.write("5\n6\n7\n10\n");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        UserComputeAPI userAPI = new UserComputeAPIImpl(
                new DataStorageComputeAPIImpl(),
                new ComputeEngineAPIImpl()
        );

        // Create a request that tells the implementation where to read from
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "collatzInput.txt"; // file with numbers
            }

            @Override
            public String getOutputDelimiter() {
                return "\n"; 
            }

            @Override
            public String getOutputDestination() {
                return "collatzOutput.txt" ; 
            }
        };

        UserComputeResult result = userAPI.processInput(request);

        // Print the result
        System.out.println("Collatz Sequences:\n" + result.getMessage());
    }
}