package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

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

            String bigInput = request.getInputString();

            // BigInteger mode
            if (bigInput != null) {
                BigInteger n;
                try {
                    n = new BigInteger(bigInput);
                } catch (Exception e) {
                    return new ComputeResult(false, "Invalid big integer: " + bigInput);
                }

                if (n.compareTo(BigInteger.ONE) < 0) {
                    return new ComputeResult(false, "Input must be a positive integer");
                }

                List<BigInteger> seq = new ArrayList<>();
                seq.add(n);

                while (!n.equals(BigInteger.ONE)) {
                    if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                        n = n.divide(BigInteger.TWO);
                    } else {
                        n = n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
                    }
                    seq.add(n);
                }

                StringBuilder out = new StringBuilder();
                for (BigInteger val : seq) {
                    out.append(val.toString()).append(",");
                }
                out.setLength(out.length() - 1); // remove last comma
                out.append("\n");

                return new ComputeResult(true, out.toString());
            }

            // Normal int mode
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
            sb.setLength(sb.length() - 1);
            sb.append("\n");

            return new ComputeResult(true, sb.toString());

        } catch (Exception e) {
            return new ComputeResult(false, "Computation error: " + e.getMessage());
        }
    }

    public List<ComputeResult> computeCollatzBatch(List<ComputeRequest> requests)
            throws InterruptedException, ExecutionException {

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
