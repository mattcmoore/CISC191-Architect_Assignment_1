package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public CustomerResponse sendRequest(CustomerRequest request) throws Exception {
        String requestStr = CustomerRequest.toJSON(request);
        System.out.println("writing " + requestStr);
        out.println(requestStr);  // Send the request as JSON
        return CustomerResponse.fromJSON(in.readLine());  // Receive the response as JSON
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {  // Loop to allow continuous task execution
            Client client = new Client();

            try {
                // Start connection to the server
                client.startConnection("127.0.0.1", 4444);

                // Present task options to the user
                System.out.println("Select a task:");
                System.out.println("1 - 1D Array Operation");
                System.out.println("2 - 2D Array Operation");
                System.out.println("0 - Exit");

                int task = scanner.nextInt();  // User selects task

                if (task == 0) {
                    System.out.println("Exiting...");
                    break;  // Exit the loop and program
                }

                CustomerRequest request = null;  // Initialize request object

                if (task == 1) {
                    // 1D Array Operation (Using ArrayList)
                    ArrayList<Integer> params = new ArrayList<>();

                    System.out.println("Selected: 1D Array Operation");
                    System.out.println("Choose an operation: ");
                    System.out.println("1 - Fetch value at index");
                    System.out.println("2 - Update value at index");
                    System.out.println("3 - Find index of a value");
                    System.out.println("4 - Print all values");
                    System.out.println("5 - Delete value at index");
                    System.out.println("6 - Expand the array");
                    System.out.println("7 - Shrink the array");

                    int operation = scanner.nextInt();  // User selects 1D array operation

                    if (operation >= 1 && operation <= 5) {
                        System.out.println("Enter the index: ");
                        int index = scanner.nextInt();  // User enters the index
                        params.add(index);  // Add index to the array
                    }

                    if (operation == 2) {
                        // Update value at the given index
                        System.out.println("Enter the new value: ");
                        int value = scanner.nextInt();
                        params.add(value);  // Add value to the array
                    } else if (operation == 3) {
                        // Find index of a value
                        System.out.println("Enter the value to find: ");
                        int valueToFind = scanner.nextInt();
                        params.add(valueToFind);
                    } else if (operation == 5) {
                        // Delete value at the given index
                        System.out.println("Enter the index to delete: ");
                        int deleteIndex = scanner.nextInt();
                        params.add(deleteIndex);
                    } else if (operation == 6 || operation == 7) {
                        // No additional parameters needed for expand/shrink
                    }

                    request = new CustomerRequest(1, 1, operation, params);
                    System.out.println("Sending request for 1D Array Operation: " + params);

                } else if (task == 2) {
                    // 2D Array Operation (Using HashMap)
                    ArrayList<Integer> params = new ArrayList<>();

                    System.out.println("Selected: 2D Array Operation");
                    System.out.println("Choose an operation: ");
                    System.out.println("1 - Get value at (row, column)");
                    System.out.println("2 - Set value at (row, column)");
                    System.out.println("3 - Find index of a value");
                    System.out.println("4 - Print all values");
                    System.out.println("5 - Delete value at (row, column)");
                    System.out.println("6 - Expand the array");
                    System.out.println("7 - Shrink the array");

                    int operation = scanner.nextInt();  // User selects 2D array operation

                    if (operation >= 1 && operation <= 5) {
                        System.out.println("Enter row index: ");
                        int rowIndex = scanner.nextInt();  // User enters row index
                        params.add(rowIndex);  // Add row index to params

                        System.out.println("Enter column index: ");
                        int colIndex = scanner.nextInt();  // User enters column index
                        params.add(colIndex);  // Add column index to params
                    }

                    if (operation == 2) {
                        // Set value at the given (row, column)
                        System.out.println("Enter the new value: ");
                        int value = scanner.nextInt();
                        params.add(value);  // Add value to the params
                    } else if (operation == 3) {
                        // Find index of a value
                        System.out.println("Enter the value to find: ");
                        int valueToFind = scanner.nextInt();
                        params.add(valueToFind);
                    } else if (operation == 5) {
                        // No additional parameters needed for delete operation
                    } else if (operation == 6 || operation == 7) {
                        // No additional parameters needed for expand/shrink
                    }

                    request = new CustomerRequest(1, 2, operation, params);
                    System.out.println("Sending request for 2D Array Operation: " + params);

                } else {
                    System.out.println("Invalid task selection.");
                    continue;  // Start from the beginning of the loop
                }

                // Send the request to the server and print the response
                CustomerResponse response = client.sendRequest(request);
                if (response != null) {
                    System.out.println("Received result from server: " + response.getServerResposne());
                }

                // Stop the connection after each task
                client.stopConnection();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();  // Close scanner at the end of the loop
    }
}
