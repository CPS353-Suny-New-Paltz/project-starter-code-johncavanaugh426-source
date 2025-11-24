package project.tests;

import org.junit.jupiter.api.Test;
import visualizer.CollatzVisualizerFromFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollatzVisualizerTest {

    @Test
    public void testVisualizerReadsOutputFile() throws Exception {
        // Make sure the input file exists
        File inputFile = new File("chartInput.txt");
        assertTrue(inputFile.exists(), "Input file chartInput.txt should exist");

        // Run the visualizer 
        CollatzVisualizerFromFile.main(new String[]{});

        // check that output file is created
        File outputFile = new File("collatzCharts.txt");
        assertTrue(outputFile.exists(), "Visualizer should create collatzCharts.txt");
    }
}
