package project.tests;

import org.junit.jupiter.api.Test;
import project.api.conceptual.ComputeEngineAPI;
import project.impl.conceptual.ComputeEngineAPIImpl;
import project.impl.conceptual.FastComputeEngineAPIImpl;
import project.impl.conceptual.ComputeEngineOptimizedAPIImpl;
import project.impl.conceptual.ComputeEngineAPIUltraFastImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputeEngineFullBenchmarkTest {

    @Test
    public void benchmarkAllImplementations() throws Exception {
        int numTests = 10_000;
        int numRuns = 10;

        long[] originalTimes = new long[numRuns];
        long[] fastTimes = new long[numRuns];
        long[] optimizedTimes = new long[numRuns];
        long[] ultraFastTimes = new long[numRuns];

        for (int run = 0; run < numRuns; run++) {

            // Original
            ComputeEngineAPI original = new ComputeEngineAPIImpl();
            long startOriginal = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                original.computeCollatz(() -> n);
            }
            originalTimes[run] = System.currentTimeMillis() - startOriginal;

            // Fast
            ComputeEngineAPI fast = new FastComputeEngineAPIImpl();
            long startFast = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                fast.computeCollatz(() -> n);
            }
            fastTimes[run] = System.currentTimeMillis() - startFast;

            // Optimized
            ComputeEngineAPI optimized = new ComputeEngineOptimizedAPIImpl();
            long startOptimized = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                optimized.computeCollatz(() -> n);
            }
            optimizedTimes[run] = System.currentTimeMillis() - startOptimized;

            // Ultra-Fast
            ComputeEngineAPI ultraFast = new ComputeEngineAPIUltraFastImpl();
            long startUltra = System.currentTimeMillis();
            for (int i = 1; i <= numTests; i++) {
                final int n = i;
                ultraFast.computeCollatz(() -> n);
            }
            ultraFastTimes[run] = System.currentTimeMillis() - startUltra;
        }

        // Compute averages
        long avgOriginal = 0;
        long avgFast = 0;
        long avgOptimized = 0;
        long avgUltra = 0;

        for (int i = 0; i < numRuns; i++) {
            avgOriginal += originalTimes[i];
            avgFast += fastTimes[i];
            avgOptimized += optimizedTimes[i];
            avgUltra += ultraFastTimes[i];
        }
        avgOriginal /= numRuns;
        avgFast /= numRuns;
        avgOptimized /= numRuns;
        avgUltra /= numRuns;

        double improvementOptimized = ((double) avgOriginal - avgOptimized) / avgOriginal * 100;
        double improvementUltra = ((double) avgOriginal - avgUltra) / avgOriginal * 100;

        // Write results to file
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("checkpoint9output.txt")))) {
            writer.println("Benchmark results for " + numRuns + " runs with " + numTests + " Collatz sequences each:");

            writer.println("Original times (ms):");
            for (long t : originalTimes) {
                writer.println(t);
            }

            writer.println("Fast times (ms):");
            for (long t : fastTimes) {
                writer.println(t);
            }

            writer.println("Optimized times (ms):");
            for (long t : optimizedTimes) {
                writer.println(t);
            }

            writer.println("Ultra-Fast times (ms):");
            for (long t : ultraFastTimes) {
                writer.println(t);
            }

            writer.println("Average Original: " + avgOriginal + " ms");
            writer.println("Average Fast: " + avgFast + " ms");
            writer.println("Average Optimized: " + avgOptimized + " ms");
            writer.println("Average Ultra-Fast: " + avgUltra + " ms");
            writer.println("Optimized Improvement vs Original: " + String.format("%.2f", improvementOptimized) + "%");
            writer.println("Ultra-Fast Improvement vs Original: " + String.format("%.2f", improvementUltra) + "%");
        }

        // Print summary to console
        System.out.println("Average Original: " + avgOriginal + " ms");
        System.out.println("Average Fast: " + avgFast + " ms");
        System.out.println("Average Optimized: " + avgOptimized + " ms");
        System.out.println("Average Ultra-Fast: " + avgUltra + " ms");
        System.out.println("Optimized Improvement vs Original: " + String.format("%.2f", improvementOptimized) + "%");
        System.out.println("Ultra-Fast Improvement vs Original: " + String.format("%.2f", improvementUltra) + "%");

        // Assert at least 10% improvement for the graded version
        assertTrue(avgOptimized <= avgOriginal * 0.9,
                "Optimized implementation did not achieve at least 10% improvement over original");
    }
}
