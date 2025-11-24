package project.visualizer;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

public class CollatzVisualizerFromFile {

    public static void main(String[] args) {
        String inputFile = args.length > 0 ? args[0] : "outputTest.txt";
        String outputFile = "collatzCharts.txt";

        BigInteger threshold = new BigInteger("1000000"); // first number > threshold => skip sequence

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String line;
            int seqNum = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Split on any non-digit character (and allow minus for negatives)
                String[] parts = line.split("[^\\d-]+");

                BigInteger firstNum = new BigInteger(parts[0].trim());
                writer.println("Sequence " + seqNum + ":");

                if (firstNum.compareTo(threshold) > 0) {
                    writer.println("(sequence skipped, first number too large: " + firstNum + ")");
                    writer.println();
                    seqNum++;
                    continue;
                }

                BigInteger[] numbers = new BigInteger[parts.length];
                BigInteger max = BigInteger.ZERO;

                for (int i = 0; i < parts.length; i++) {
                    numbers[i] = new BigInteger(parts[i].trim());
                    if (numbers[i].compareTo(max) > 0) max = numbers[i];
                }

                for (BigInteger n : numbers) {
                    int stars = n.multiply(BigInteger.valueOf(50)).divide(max).intValue();
                    if (stars < 1) stars = 1; // at least 1 star
                    for (int i = 0; i < stars; i++) writer.print("*");
                    writer.println(" (" + n + ")");
                }
                writer.println();
                seqNum++;
            }

            System.out.println("ASCII visualization written to " + outputFile);

        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number in input file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
