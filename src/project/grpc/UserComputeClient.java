package project.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class UserComputeClient {

    public static void main(String[] args) {
        System.out.println("UserComputeClient started...");

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("How many requests do you want to make? ");
            int requestCount = Integer.parseInt(scanner.nextLine().trim());

            // Connect to UserComputeServer
            String target = "localhost:50051";
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                    .usePlaintext()
                    .build();

            UserComputeServiceGrpc.UserComputeServiceBlockingStub stub =
                    UserComputeServiceGrpc.newBlockingStub(channel);

            for (int i = 1; i <= requestCount; i++) {
                System.out.println("\n--- Request " + i + " ---");

                // Choose input mode
                System.out.print("Use file input or memory input? (file/memory): ");
                String mode = scanner.nextLine().trim().toLowerCase();

                String inputSource;

                if (mode.equals("memory")) {
                    System.out.print("Enter numbers separated by spaces or commas: ");
                    String numbers = scanner.nextLine();

                    try {
                        File tempFile = File.createTempFile("temp_input", ".txt");
                        tempFile.deleteOnExit();
                        String content = numbers.replaceAll("[,\\s]+", "\n");
                        Files.write(tempFile.toPath(), content.getBytes());
                        inputSource = tempFile.getAbsolutePath();
                    } catch (IOException e) {
                        System.err.println("Failed to create temporary file: " + e.getMessage());
                        continue;
                    }
                } else {
                    System.out.print("Enter the input .txt file: ");
                    File inputFile = new File(scanner.nextLine());
                    if (!inputFile.exists()) {
                        System.err.println("Input file does not exist: " + inputFile.getAbsolutePath());
                        continue;
                    }
                    inputSource = inputFile.getAbsolutePath();
                }

                System.out.print("Enter the output .txt file: ");
                File outputFile = new File(scanner.nextLine());
                String outputDestination = outputFile.getAbsolutePath();

                System.out.print("Enter the output delimiter (default is ','): ");
                String delimiter = scanner.nextLine();
                if (delimiter.isEmpty()) {
                    delimiter = ",";
                }

                // Build gRPC request
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

                // Read and display output file with 100-line check
                if (outputFile.exists()) {
                    try {
                        long lineCount = Files.lines(outputFile.toPath()).count();

                        if (lineCount > 100) {
                            System.out.println("Output too large to display (" + lineCount + " lines).");
                            System.out.println("Output written to: " + outputFile.getAbsolutePath());
                        } else {
                            System.out.println("Output written to: " + outputFile.getAbsolutePath());
                            System.out.println("Contents of the output file:");
                            Files.lines(outputFile.toPath()).forEach(System.out::println);
                        }
                    } catch (IOException e) {
                        System.err.println("Unable to read output file: " + e.getMessage());
                    }
                } else {
                    System.out.println("Output file was not created.");
                }
            }

            channel.shutdown();

        } finally {
            scanner.close();
        }
    }
}
