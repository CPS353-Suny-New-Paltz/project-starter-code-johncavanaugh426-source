package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ComputeEngineAPIUltraFastImpl implements ComputeEngineAPI {

    private final Map<Long, long[]> cache = new HashMap<>();

    public ComputeEngineAPIUltraFastImpl() {}

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        try {
            if (request == null) return new ComputeResult(false, "Request cannot be null");

            BigInteger n;
            if (request.getInputString() != null) {
                n = new BigInteger(request.getInputString());
            } else {
                int small = request.getInputNumber();
                if (small <= 0) return new ComputeResult(false, "Input must be positive");
                n = BigInteger.valueOf(small);
            }

            StringBuilder sb = new StringBuilder();

            if (n.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
                long current = n.longValue();
                // check cache
                if (cache.containsKey(current)) {
                    for (long val : cache.get(current)) sb.append(val).append(',');
                    sb.setCharAt(sb.length() - 1, '\n'); // replace last comma
                } else {
                    // compute sequence
                    long[] sequence = new long[500]; // rough initial size
                    int index = 0;
                    while (current != 1L) {
                        if (index >= sequence.length) {
                            long[] newSeq = new long[sequence.length * 2];
                            System.arraycopy(sequence, 0, newSeq, 0, sequence.length);
                            sequence = newSeq;
                        }
                        sequence[index++] = current;
                        current = (current & 1) == 0 ? current / 2 : current * 3 + 1;
                    }
                    sequence[index++] = 1L;

                    // cache the full sequence
                    long[] finalSeq = new long[index];
                    System.arraycopy(sequence, 0, finalSeq, 0, index);
                    cache.put(n.longValue(), finalSeq);

                    // append to StringBuilder
                    for (long val : finalSeq) sb.append(val).append(',');
                    sb.setCharAt(sb.length() - 1, '\n'); // replace last comma
                }
            } else {
                // fallback BigInteger path
                while (!n.equals(BigInteger.ONE)) {
                    sb.append(n).append(',');
                    n = n.mod(BigInteger.TWO).equals(BigInteger.ZERO) ? n.divide(BigInteger.TWO) : n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
                }
                sb.append("1\n");
            }

            return new ComputeResult(true, sb.toString());

        } catch (Exception e) {
            return new ComputeResult(false, "Computation error: " + e.getMessage());
        }
    }
}
