package project.impl.conceptual;

import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputeEngineAPIImpl implements ComputeEngineAPI {

    public ComputeEngineAPIImpl() {
    }

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        try {
            if (request == null) {
                return new ComputeResult(false, "Request cannot be null");
            }

            BigInteger n;

            if (request.getInputString() != null) {
                // Big number path:
                // If the user provides a string, we assume it may be large.
                n = new BigInteger(request.getInputString());
            } else {
                // Use the original int-based method.
                int small = request.getInputNumber();
                if (small <= 0) {
                    return new ComputeResult(false, "Input must be a positive integer");
                }
                n = BigInteger.valueOf(small);
            }

            if (n.compareTo(BigInteger.ZERO) <= 0) {
                return new ComputeResult(false, "Input must be a positive integer");
            }

            List<BigInteger> sequence = new ArrayList<>();
            sequence.add(n);

            while (!n.equals(BigInteger.ONE)) {
                if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                    n = n.divide(BigInteger.TWO);
                } else {
                    n = n.multiply(BigInteger.valueOf(3)).add(BigInteger.ONE);
                }
                sequence.add(n);
            }

            String resultString = sequence.stream()
                    .map(BigInteger::toString)
                    .collect(Collectors.joining(",")) + "\n";

            return new ComputeResult(true, resultString);

        } catch (Exception e) {
            return new ComputeResult(false, "Computation error: " + e.getMessage());
        }
    }
}
