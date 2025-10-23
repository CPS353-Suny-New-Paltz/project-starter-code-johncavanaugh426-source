package testHarness;

import project.api.network.UserComputeAPI;
import project.impl.network.UserComputeAPIImpl;

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

    // TODO 1 fixed: use your @NetworkAPI interface type
    private UserComputeAPI coordinator;

    @BeforeEach
    public void initializeComputeEngine() {
        // TODO 2 fixed: instantiate the implementation of your @NetworkAPI
        coordinator = new UserComputeAPIImpl();
    }

    public void cleanup() {
        // Nothing to shut down for single-threaded version
    }

    @Test
    public void compareMultiAndSingleThreaded() throws Exception {
        int numThreads = 4; // renamed from nThreads
        List<TestUser> testUsers = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            testUsers.add(new TestUser(coordinator));
        }

        // Run single-threaded
        String singleThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp"; // renamed from sFilePrefix
        for (int i = 0; i < numThreads; i++) {
            File singleThreadedOut = new File(singleThreadFilePrefix + i);
            singleThreadedOut.deleteOnExit();
            testUsers.get(i).run(singleThreadedOut.getCanonicalPath());
        }

        // Run multi-threaded
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<?>> results = new ArrayList<>();
        String multiThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp"; // renamed from mFilePrefix
        for (int i = 0; i < numThreads; i++) {
            File multiThreadedOut = new File(multiThreadFilePrefix + i);
            multiThreadedOut.deleteOnExit();
            String multiThreadFilePath = multiThreadedOut.getCanonicalPath(); // renamed from mFilePath
            TestUser testUser = testUsers.get(i);
            results.add(threadPool.submit(() -> testUser.run(multiThreadFilePath)));
        }

        results.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Check that the output is the same for multi-threaded and single-threaded
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

    @Test
    public void smokeTest() {
        // Optional smoke test with simple strings
        List<String> requests = List.of("test1", "test2", "test3");
        List<String> results = new ArrayList<>();
        for (String req : requests) {
            results.add(req); // placeholder
        }
        assertEquals(requests.size(), results.size());
    }
}
