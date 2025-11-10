package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

/**
 * FastComputeEngineAPIImpl
 *
 * Optimization:
 * - Identified CPU bottleneck: computing many Collatz sequences sequentially in ComputeEngineAPIImpl
 * - Fixed by parallelizing the computation using a fixed thread pool.
 * - Each sequence computation is independent, so it benefits from multithreading.
 */
public class FastComputeEngineAPIImpl implements ComputeEngineAPI {

    private static final int THREAD_LIMIT = 8;

    public FastComputeEngineAPIImpl() {
    }

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        try {
            if (request == null) {
                return new ComputeResult(false, "Request cannot be null");
            }

            int n = request.getInputNumber();
            if (n <= 0) {
                return new ComputeResult(false, "Input must be a positive integer");
            }

            List<Integer> sequence = new ArrayList<>();
            sequence.add(n);

            while (n != 1) {
                if (n % 2 == 0) {
                    n /= 2;
                } else {
                    n = 3 * n + 1;
                }
                sequence.add(n);
            }

            StringBuilder sb = new StringBuilder();
            for (int num : sequence) {
                sb.append(num).append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); // remove last comma
            }
            sb.append("\n");

            return new ComputeResult(true, sb.toString());

        } catch (Exception e) {
            return new ComputeResult(false, "Computation error: " + e.getMessage());
        }
    }

    /**
     * Helper method for benchmarking: compute multiple sequences in parallel
     */
    public List<ComputeResult> computeCollatzBatch(List<ComputeRequest> requests) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
        List<Future<ComputeResult>> futures = new ArrayList<>();

        for (ComputeRequest req : requests) {
            futures.add(executor.submit(() -> computeCollatz(req)));
        }

        List<ComputeResult> results = new ArrayList<>();
        for (Future<ComputeResult> f : futures) {
            results.add(f.get());
        }

        executor.shutdown();
        return results;
    }
}
