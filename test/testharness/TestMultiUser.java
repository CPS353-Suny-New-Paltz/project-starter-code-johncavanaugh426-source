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

        for (int i = 0; i < numThreads; i++) {
            singleThreadUsers.add(new TestUser(singleThreadCoordinator));
            multiThreadUsers.add(new TestUser(multiThreadCoordinator));
        }

        // Single-threaded run
        System.out.println("Running single-threaded version...");
        String singleThreadFilePrefix = "singleThreadOut.tmp";
        for (int i = 0; i < numThreads; i++) {
            System.out.println("[Single-thread] Running user " + i + " on thread: " + Thread.currentThread().getName());
            File singleThreadedOut = new File(singleThreadFilePrefix + i);
            singleThreadedOut.deleteOnExit();
            singleThreadUsers.get(i).run(singleThreadedOut.getCanonicalPath());
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

            int userId = i;
            results.add(threadPool.submit(() -> {
                System.out.println("[Multi-thread] Running user " + userId + " on thread: " + Thread.currentThread().getName());
                testUser.run(multiThreadFilePath);
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
