package project.impl.conceptual;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputeEngineAPIImpl implements ComputeEngineAPI {

    public ComputeEngineAPIImpl() {
    }

    @Override
    public ComputeResult computeCollatz(ComputeRequest request) {
        int n = request.getInputNumber();
        if (n <= 0) {
            return new ComputeResult(false, "Input must be a positive integer");
        }

        List<Integer> sequence = new ArrayList<>();
        sequence.add(n);

        while (n != 1) {
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
            sequence.add(n);
        }

        // Join the numbers into a comma-separated string
        String resultString = sequence.stream()
                                      .map(String::valueOf)
                                      .collect(Collectors.joining(","));

        return new ComputeResult(true, resultString);
    }
}
