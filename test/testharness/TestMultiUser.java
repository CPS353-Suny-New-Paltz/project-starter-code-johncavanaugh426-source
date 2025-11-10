package testharness;

import project.api.network.UserComputeAPI;
import project.impl.network.UserComputeAPIImpl;
import project.impl.network.UserComputeAPIMultiThreaded;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMultiUser {

    private UserComputeAPI singleThreadCoordinator;
    private UserComputeAPI multiThreadCoordinator;

    @BeforeEach
    public void initializeComputeEngine() {
        singleThreadCoordinator = new UserComputeAPIImpl();
        multiThreadCoordinator = new UserComputeAPIMultiThreaded();
    }

    @Test
    public void compareMultiAndSingleThreaded() throws Exception {
        int numThreads = 4;
        System.out.println("Starting compareMultiAndSingleThreaded test...");
        System.out.println("Using " + numThreads + " users.");

        List<TestUser> singleThreadUsers = new ArrayList<>();
        List<TestUser> multiThreadUsers = new ArrayList<>();
        List<File> tempInputFiles = new ArrayList<>();

        // Create temporary input files for each user
        for (int i = 0; i < numThreads; i++) {
            singleThreadUsers.add(new TestUser(singleThreadCoordinator));
            multiThreadUsers.add(new TestUser(multiThreadCoordinator));

            File inputFile = File.createTempFile("input", i + ".tmp");
            inputFile.deleteOnExit();
            Files.writeString(inputFile.toPath(), "5\n3\n10\n");
            tempInputFiles.add(inputFile);
        }

        // Single-threaded run
        System.out.println("Running single-threaded version...");
        String singleThreadFilePrefix = "singleThreadOut.tmp";

        for (int i = 0; i < numThreads; i++) {
            System.out.println("[Single-thread] Running user " + i + " on thread: " + Thread.currentThread().getName());

            File singleThreadedOut = new File(singleThreadFilePrefix + i);
            singleThreadedOut.deleteOnExit();

            TestUser user = singleThreadUsers.get(i);
            user.run(singleThreadedOut.getCanonicalPath(), tempInputFiles.get(i).getCanonicalPath());
        }

        // Multi-threaded run
        System.out.println("\nRunning multi-threaded version...");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<?>> results = new ArrayList<>();
        String multiThreadFilePrefix = "multiThreadOut.tmp";

        for (int i = 0; i < numThreads; i++) {
            File multiThreadedOut = new File(multiThreadFilePrefix + i);
            multiThreadedOut.deleteOnExit();
            String multiThreadFilePath = multiThreadedOut.getCanonicalPath();
            TestUser testUser = multiThreadUsers.get(i);
            String inputPath = tempInputFiles.get(i).getCanonicalPath();

            int userId = i;
            results.add(threadPool.submit(() -> {
                System.out.println("[Multi-thread] Running user " + userId + " on thread: " + Thread.currentThread().getName());
                testUser.run(multiThreadFilePath, inputPath);
            }));
        }

        // Wait for all threads to finish
        for (Future<?> f : results) {
            f.get();
        }
        threadPool.shutdown();

        System.out.println("Multi-threaded run finished.\n");

        // Compare outputs
        List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix, numThreads);
        List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix, numThreads);

        // Display outputs for confirmation
        System.out.println("\n=== Single-threaded Outputs ===");
        for (String line : singleThreaded) {
            System.out.println(line);
        }

        System.out.println("\n=== Multi-threaded Outputs ===");
        for (String line : multiThreaded) {
            System.out.println(line);
        }

        // Assert equality
        assertEquals(singleThreaded, multiThreaded);
    }

    private List<String> loadAllOutput(String prefix, int numThreads) throws IOException {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            File outFile = new File(prefix + i);
            result.addAll(Files.readAllLines(outFile.toPath()));
        }
        return result;
    }
}
