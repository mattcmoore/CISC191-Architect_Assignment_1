// File: server/Server.java
package edu.sdccd.cisc191.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.common.Task;
import edu.sdccd.cisc191.common.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5555;
    private DataStore dataStore;
    private UserManager userManager;
    private TaskManager taskManager;
    private ObjectMapper objectMapper;

    public Server() {
        dataStore = new DataStore();
        userManager = new UserManager(dataStore.getUsers());
        taskManager = new TaskManager();
        // Initialize TaskManager with existing tasks
        for (Task task : dataStore.getTasks()) {
            taskManager.addTask(task);
        }
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // For Java 8 Date/Time
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, userManager, taskManager, dataStore, objectMapper);
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
