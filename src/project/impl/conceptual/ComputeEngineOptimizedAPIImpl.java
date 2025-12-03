package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputeEngineOptimizedAPIImpl implements ComputeEngineAPI {

    // Memoization cache for BigInteger sequences
    private final Map<BigInteger, List<BigInteger>> memo = new HashMap<>();

    // Threshold to switch from long to BigInteger
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    public ComputeEngineOptimizedAPIImpl() {
    }

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        try {
            if (request == null) {
                return new ComputeResult(false, "Request cannot be null");
            }

            BigInteger n;
            if (request.getInputString() != null) {
                n = new BigInteger(request.getInputString());
            } else {
                int small = request.getInputNumber();
                if (small <= 0) {
                    return new ComputeResult(false, "Input must be a positive integer");
                }
                n = BigInteger.valueOf(small);
            }

            List<BigInteger> sequence;
            if (n.compareTo(LONG_MAX) < 0) {
                // Use primitive optimization for numbers that fit in a long
                sequence = computeCollatzLong(n.longValue());
            } else {
                sequence = computeCollatzBigInteger(n);
            }

            String resultString = sequence.stream()
                    .map(BigInteger::toString)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("") + "\n";

            return new ComputeResult(true, resultString);

        } catch (Exception e) {
            return new ComputeResult(false, "Computation error: " + e.getMessage());
        }
    }

    // Optimized for long primitives
    private List<BigInteger> computeCollatzLong(long n) {
        List<BigInteger> sequence = new ArrayList<>();
        sequence.add(BigInteger.valueOf(n));

        while (n != 1) {
            if (n % 2 == 0) {
                n /= 2;
            } else {
                n = 3 * n + 1;
            }
            sequence.add(BigInteger.valueOf(n));
        }

        return sequence;
    }
    private List<BigInteger> computeCollatzBigInteger(BigInteger n) {
        if (memo.containsKey(n)) {
            return new ArrayList<>(memo.get(n));
        }

        List<BigInteger> sequence = new ArrayList<>();
        sequence.add(n);

        BigInteger current = n;
        while (!current.equals(BigInteger.ONE)) {
            if (memo.containsKey(current)) {
                List<BigInteger> cachedTail = memo.get(current);
                // Skip the first element since it's already in sequence
                sequence.addAll(cachedTail.subList(1, cachedTail.size()));
                break;
            }

            if (current.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                current = current.divide(BigInteger.TWO);
            } else {
                current = current.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
            }

            sequence.add(current);
        }

        memo.put(n, new ArrayList<>(sequence));
        return sequence;
    }
}
