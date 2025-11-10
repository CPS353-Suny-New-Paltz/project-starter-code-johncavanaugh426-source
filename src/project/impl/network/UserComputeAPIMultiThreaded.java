package project.impl.network;

import project.api.network.UserComputeAPI;
import project.api.network.UserComputeRequest;
import project.api.network.UserComputeResult;
import project.api.process.DataStorageComputeAPI;
import project.api.process.ProcessRequest;
import project.api.process.ProcessResult;
import project.impl.process.DataStorageComputeAPIImpl;
import project.api.conceptual.ComputeEngineAPI;
import project.api.conceptual.ComputeRequest;
import project.api.conceptual.ComputeResult;
import project.impl.conceptual.ComputeEngineAPIImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserComputeAPIMultiThreaded implements UserComputeAPI
{

    private final DataStorageComputeAPI dataStore;
    private final ComputeEngineAPI computeEngine;
    private static final int THREAD_LIMIT = 5;

    public UserComputeAPIMultiThreaded()
    {
        this(new DataStorageComputeAPIImpl(), new ComputeEngineAPIImpl());
    }

    public UserComputeAPIMultiThreaded(DataStorageComputeAPI dataStore, ComputeEngineAPI computeEngine)
    {
        this.dataStore = dataStore;
        this.computeEngine = computeEngine;
    }

    @Override
    public UserComputeResult processInput(UserComputeRequest request)
    {
        try
        {
            if (request == null)
            {
                return new UserComputeResult(false, "Request cannot be null");
            }

            if (request.getInputSource() == null || request.getInputSource().trim().isEmpty())
            {
                return new UserComputeResult(false, "Input source must be provided");
            }

            String inputPath = request.getInputSource();
            String outputPath = request.getOutputDestination();
            String delimiter = request.getOutputDelimiter() != null ? request.getOutputDelimiter() : ",";

            // Read data using the data storage layer
            List<Integer> inputData = Files.lines(Paths.get(inputPath))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
            List<Future<String>> futures = new ArrayList<>();

            for (int number : inputData)
            {
                futures.add(executor.submit(() ->
                {
                    ComputeRequest computeRequest = () -> number;
                    ComputeResult computeResult = computeEngine.computeCollatz(computeRequest);

                    if (!computeResult.isSuccess())
                    {
                        throw new RuntimeException("Computation failed for " + number);
                    }

                    return computeResult.getSequence().replace(",", delimiter);
                }));
            }

            StringBuilder resultBuilder = new StringBuilder();

            for (Future<String> f : futures)
            {
                resultBuilder.append(f.get()).append(System.lineSeparator());
            }

            executor.shutdown();

            // Write results using DataStorageComputeAPI
            String finalOutput = resultBuilder.toString().trim();

            ProcessRequest writeRequest = new ProcessRequest()
            {
                @Override
                public List<Integer> getInputData()
                {
                    return null;
                }

                @Override
                public String getOutputDestination()
                {
                    return outputPath;
                }

                @Override
                public String getDelimiter()
                {
                    return delimiter;
                }

                @Override
                public String getComputedResults()
                {
                    return finalOutput;
                }

                @Override
                public String getInputSource()
                {
                    return inputPath;
                }
            };

            ProcessResult writeResult = dataStore.processData(writeRequest);

            if (!writeResult.isSuccess())
            {
                return new UserComputeResult(false, "Write failed: " + writeResult.getMessage());
            }

            return new UserComputeResult(true, "Multi-threaded computation completed successfully");
        }
        catch (Exception e)
        {
            return new UserComputeResult(false, "Error: " + e.getMessage());
        }
    }
}
