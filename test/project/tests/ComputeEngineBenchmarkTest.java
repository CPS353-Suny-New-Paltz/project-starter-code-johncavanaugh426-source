package project.tests;

import org.junit.jupiter.api.Test;
import project.api.conceptual.ComputeEngineAPI;
import project.impl.conceptual.ComputeEngineAPIImpl;
import project.impl.conceptual.FastComputeEngineAPIImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputeEngineBenchmarkTest {

    @Test
    public void benchmarkCollatzMultipleRuns() throws Exception {
        int numTests = 50_000; // number of Collatz computations
        int numRuns = 10; // number of times to repeat the experiment
        long[] originalTimes = new long[numRuns];
        long[] fastTimes = new long[numRuns];

        // --- Run benchmark multiple times ---
        for (int run = 0; run < numRuns; run++) {
            // Original implementation
            ComputeEngineAPI originalEngine = new ComputeEngineAPIImpl();
            long startOriginal = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                originalEngine.computeCollatz(() -> n);
            }
            long endOriginal = System.currentTimeMillis();
            originalTimes[run] = endOriginal - startOriginal;

            // Fast implementation
            FastComputeEngineAPIImpl fastEngine = new FastComputeEngineAPIImpl();
            long startFast = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                fastEngine.computeCollatz(() -> n);
            }
            long endFast = System.currentTimeMillis();
            fastTimes[run] = endFast - startFast;
        }

        // --- Compute averages ---
        long avgOriginal = 0;
        long avgFast = 0;
        for (int i = 0; i < numRuns; i++) {
            avgOriginal += originalTimes[i];
            avgFast += fastTimes[i];
        }
        avgOriginal /= numRuns;
        avgFast /= numRuns;
        double improvement = ((double) avgOriginal - avgFast) / avgOriginal * 100;

        // --- Write results to checkpoint8output.txt ---
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("checkpoint8output.txt")))) {
            writer.println("Benchmark results for " + numRuns + " runs with " + numTests + " Collatz sequences each:");
            writer.println("Original ComputeEngineAPIImpl times (ms):");
            for (long t : originalTimes) {
                writer.println(t);
            }
            writer.println("Fast ComputeEngineAPIImpl times (ms):");
            for (long t : fastTimes) {
                writer.println(t);
            }
            writer.println("Average Original: " + avgOriginal + " ms");
            writer.println("Average Fast: " + avgFast + " ms");
            writer.println("Performance improvement: " + String.format("%.2f", improvement) + "%");
        }

        // --- Output summary to console ---
        System.out.println("Benchmark completed. Results saved to checkpoint8output.txt");
        System.out.println("Average Original: " + avgOriginal + " ms");
        System.out.println("Average Fast: " + avgFast + " ms");
        System.out.println("Performance improvement: " + String.format("%.2f", improvement) + "%");

        // Optional assertion to verify at least 10% improvement
        assertTrue(avgFast <= avgOriginal * 0.9,
                "Fast implementation did not achieve at least 10% improvement");
    }
}
