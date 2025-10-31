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

        // Step 1: Connect to the gRPC server
        String target = "localhost:50051";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        UserComputeServiceGrpc.UserComputeServiceBlockingStub stub =
                UserComputeServiceGrpc.newBlockingStub(channel);

        // Step 2: user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the input .txt file: ");
        String inputSource = scanner.nextLine();
        System.out.print("Enter the output .txt file: ");
        String outputDestination = scanner.nextLine();
        System.out.print("Enter the output delimiter: ");
        String delimiter = scanner.nextLine();

        // Step 3: Build the request
        UserComputeRequestMessage.Builder requestBuilder = UserComputeRequestMessage.newBuilder()
                .setInputSource(inputSource)
                .setOutputDestination(outputDestination);

        if (!delimiter.isEmpty()) {
            requestBuilder.setOutputDelimiter(delimiter);
        }

        UserComputeRequestMessage request = requestBuilder.build();

        // Step 4: Send the request and handle the response
        try {
            UserComputeResultMessage response = stub.processInput(request);
            System.out.println("Task completed successfully: " + response.getSuccess());
            if (response.hasMessage()) {
                System.out.println("Server message: " + response.getMessage());
            }

            // Step 5: Read the output file and display results
            File outputFile = new File(outputDestination);
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

        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
        }

        // Step 6: Clean up
        channel.shutdown();
        scanner.close();
    }
}
