package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Server {
    private static final String SAVE_FILE = "data.ser"; // File to save data
    private ServerSocket serverSocket;
    private ArrayOperations arrayOps;
    private TwoDArrayOperations twoDArrayOps;

    public Server() {
        // Load saved data from disk or create new instances
        loadDataFromDisk();
    }

    public void start(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started, waiting for connections...");

        // Continuously accept new client connections
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
            // Create a new thread to handle the client, passing the shared instance
            new ClientHandler(clientSocket, arrayOps, twoDArrayOps).start();
        }
    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        // Save data to disk when the server stops
        saveDataToDisk();
    }

    private void saveDataToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(arrayOps);
            oos.writeObject(twoDArrayOps);
            System.out.println("Data saved to disk.");
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadDataFromDisk() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            arrayOps = (ArrayOperations) ois.readObject();
            twoDArrayOps = (TwoDArrayOperations) ois.readObject();
            System.out.println("Data loaded from disk.");
        } catch (FileNotFoundException e) {
            // If file not found, initialize fresh instances
            arrayOps = ArrayOperations.getInstance(); // Use Singleton pattern
            twoDArrayOps = new TwoDArrayOperations(5, 5);
            System.out.println("No saved data found, initializing new instances.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(4444);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Inner class to handle client connections
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ArrayOperations arrayOps; // Reference to shared instance
        private TwoDArrayOperations twoDArrayOps; // Reference to shared instance

        public ClientHandler(Socket socket, ArrayOperations arrayOps, TwoDArrayOperations twoDArrayOps) {
            this.clientSocket = socket;
            this.arrayOps = arrayOps; // Initialize with shared instance
            this.twoDArrayOps = twoDArrayOps; // Initialize with shared instance
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    CustomerRequest request = CustomerRequest.fromJSON(inputLine);
                    String response = processCustomerRequest(request);
                    CustomerResponse customerResponse = new CustomerResponse(request.getId(), "Jane", "Doe");
                    customerResponse.setServerResposne(response);
                    out.println(CustomerResponse.toJSON(customerResponse));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private String processCustomerRequest(CustomerRequest request) {
            int task = request.getTask();
            String message = "Operation Success";
            int operation = request.getOperation();
            List<Integer> params = request.getParams();

            System.out.println("arrayOps ="+arrayOps);

            if (task == 1) { // Check if it's a 1D Array Operation


                switch (operation) {
                    case 1: // Fetch value at index
                        if (params.size() == 1) {
                            int index = params.get(0);
                            try {
                                int value = arrayOps.getAtIndex(index);
                                message = "Value at index " + index + ": " + value;
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for fetching value at index.";
                        }
                        break;

                    case 2: // Update value at index
                        if (params.size() == 2) {
                            int index = params.get(0);
                            int value = params.get(1);
                            try {
                                arrayOps.setAtIndex(index, value);
                                message = "Updated index " + index + " with value " + value;
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for updating value at index.";
                        }
                        break;

                    case 3: // Find index of a value
                        if (params.size() == 1) {
                            int value = params.get(0);
                            int index = arrayOps.findIndexOf(value);
                            message = (index != -1) ? "Value " + value + " found at index: " + index
                                    : "Value " + value + " not found.";
                        } else {
                            message = "Invalid parameters for finding index of a value.";
                        }
                        break;

                    case 4: // Print all values
                        int[] allValues = arrayOps.printAll();
                        message = "Array values: " + Arrays.toString(allValues);
                        break;

                    case 5: // Delete value at index
                        if (params.size() == 1) {
                            int index = params.get(0);
                            try {
                                arrayOps.deleteAtIndex(index);
                                message = "Deleted value at index " + index;
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for deleting value at index.";
                        }
                        break;

                    case 6: // Expand the array
                        arrayOps.expandArray();
                        message = "Array expanded.";
                        break;

                    case 7: // Shrink the array
                        try {
                            arrayOps.shrinkArray();
                            message = "Array shrunk.";
                        } catch (IllegalStateException e) {
                            message = e.getMessage();
                        }
                        break;

                    default:
                        message = "Invalid operation.";
                        break;
                }
            }else if (task == 2) {// Task 2: 2D Array Operations

                switch (operation) {
                    case 1: // Get element at specific index (row, col)
                        if (params.size() == 2) {
                            int rowIndex = params.get(0);
                            int colIndex = params.get(1);
                            try {
                                message = "Element at [" + rowIndex + "][" + colIndex + "]: " + twoDArrayOps.getAtIndex(rowIndex, colIndex);
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for fetching element at index.";
                        }
                        break;

                    case 2: // Set element at specific index (row, col, value)
                        if (params.size() == 3) {
                            int rowIndex = params.get(0);
                            int colIndex = params.get(1);
                            int value = params.get(2);
                            try {
                                twoDArrayOps.setAtIndex(rowIndex, colIndex, value);
                                message = "Set value " + value + " at [" + rowIndex + "][" + colIndex + "]";
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for setting element at index.";
                        }
                        break;

                    case 3: // Print all elements of the 2D array
                        message = "2D Array elements:\n" + twoDArrayOps.printAll();
                        break;

                    case 4: // Find index of a specific value
                        if (params.size() == 1) {
                            int value = params.get(0);
                            message = twoDArrayOps.findIndexOf(value);
                        } else {
                            message = "Invalid parameters for finding element.";
                        }
                        break;

                    case 5: // Delete element at specific index (row, col)
                        if (params.size() == 2) {
                            int rowIndex = params.get(0);
                            int colIndex = params.get(1);
                            try {
                                twoDArrayOps.deleteAtIndex(rowIndex, colIndex);
                                message = "Deleted element at [" + rowIndex + "][" + colIndex + "]";
                            } catch (IndexOutOfBoundsException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for deleting element at index.";
                        }
                        break;

                    case 6: // Expand the 2D array (newRows, newCols)
                        if (params.size() == 2) {
                            int newRows = params.get(0);
                            int newCols = params.get(1);
                            twoDArrayOps.expand(newRows, newCols);
                            message = "Array expanded to [" + newRows + "][" + newCols + "]";
                        } else {
                            message = "Invalid parameters for expanding the array.";
                        }
                        break;

                    case 7: // Shrink the 2D array (newRows, newCols)
                        if (params.size() == 2) {
                            int newRows = params.get(0);
                            int newCols = params.get(1);
                            try {
                                twoDArrayOps.shrink(newRows, newCols);
                                message = "Array shrunk to [" + newRows + "][" + newCols + "]";
                            } catch (IllegalArgumentException e) {
                                message = e.getMessage();
                            }
                        } else {
                            message = "Invalid parameters for shrinking the array.";
                        }
                        break;

                    default:
                        message = "Invalid 2D array operation.";
                        break;
                }

            }
            saveDataToDisk();
            return message;
        }
    }
}
