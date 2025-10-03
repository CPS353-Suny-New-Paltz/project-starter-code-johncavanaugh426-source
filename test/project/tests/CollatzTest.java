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
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollatzTest {

    @Test
    public void testCollatzSequences() {
        // Write test numbers into collatzInput.txt
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

        // Request with valid input, delimiter, and output destination
        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public String getInputSource() {
                return "collatzInput.txt";
            }

            @Override
            public String getOutputDelimiter() {
                return "\n"; // sequences separated by newlines
            }

            @Override
            public String getOutputDestination() {
                return "collatzOutput.txt"; // must not be null or empty
            }
        };

        UserComputeResult result = userAPI.processInput(request);

        // Print what we got
        System.out.println("Collatz Sequences:\n" + result.getMessage());

        
        try {
            String fileContent = Files.readString(Paths.get("collatzOutput.txt"));
            System.out.println("File content:\n" + fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

       
        assertTrue(result.isSuccess(), "Computation should succeed");
    }
}
