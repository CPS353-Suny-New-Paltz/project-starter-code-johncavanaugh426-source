package project.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserComputeClient {

    public static void main(String[] args) {

        System.out.println("UserComputeClient started...");

        Scanner scanner = new Scanner(System.in);

        try {
            // Prompt for input file
            System.out.print("Enter the input .txt file: ");
            File inputFile = new File(scanner.nextLine());
            if (!inputFile.exists()) {
                System.err.println("Input file does not exist: " + inputFile.getAbsolutePath());
                return;
            }
            String inputSource = inputFile.getAbsolutePath();

            // Prompt for output file
            System.out.print("Enter the output .txt file: ");
            File outputFile = new File(scanner.nextLine());
            String outputDestination = outputFile.getAbsolutePath();

            // Prompt for delimiter
            System.out.print("Enter the output delimiter (default is ','): ");
            String delimiter = scanner.nextLine();
            if (delimiter.isEmpty()) {
                delimiter = ",";
            }

            // Connect to UserComputeServer
            String target = "localhost:50051";
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();

            UserComputeServiceGrpc.UserComputeServiceBlockingStub stub =
                    UserComputeServiceGrpc.newBlockingStub(channel);

            // Build the gRPC request
            UserComputeRequestMessage request = UserComputeRequestMessage.newBuilder()
                    .setInputSource(inputSource)
                    .setOutputDestination(outputDestination)
                    .setOutputDelimiter(delimiter)
                    .build();

            // Send request
            UserComputeResultMessage response = stub.processInput(request);

            System.out.println("Task completed successfully: " + response.getSuccess());
            if (response.hasMessage()) {
                System.out.println("Server message: " + response.getMessage());
            }

            // Read and display output file
            if (outputFile.exists()) {
                System.out.println("Output written to: " + outputFile.getAbsolutePath());
                System.out.println("Contents of the output file:");
                try (Scanner fileScanner = new Scanner(outputFile)) {
                    while (fileScanner.hasNextLine()) {
                        System.out.println(fileScanner.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("Unable to read output file: " + e.getMessage());
                }
            } else {
                System.out.println("Output file was not created.");
            }

            channel.shutdown();

        } finally {
            scanner.close();
        }
    }
}
