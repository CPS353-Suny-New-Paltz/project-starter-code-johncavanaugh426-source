package project.tests;

import org.junit.jupiter.api.Test;
import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.impl.network.UserComputeAPIImpl;
import project.impl.process.DataStorageComputeAPIImpl;
import project.impl.conceptual.FastComputeEngineAPIImpl;

import java.io.FileWriter;
import java.io.IOException;

public class CollatzTest {

    @Test
    public void testCollatzSequences() {
        try (FileWriter writer = new FileWriter("collatzInput.txt")) {
            writer.write("1\n");
            writer.write("5\n");
            writer.write("7\n");
            writer.write("100000000\n"); 
            writer.write("99999999999999999999999999999999\n");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        UserComputeAPI userAPI = new UserComputeAPIImpl(
                new DataStorageComputeAPIImpl(),
                new FastComputeEngineAPIImpl()  
        );

        // Create the request
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "collatzInput.txt";  // file containing numbers
            }

            @Override
            public String getOutputDelimiter() {
                return "$";
            }

            @Override
            public String getOutputDestination() {
                return "collatzOutput.txt";
            }
        };

        UserComputeResult result = userAPI.processInput(request);

        System.out.println("Collatz Sequences:\n" + result.getMessage());
    }
}
