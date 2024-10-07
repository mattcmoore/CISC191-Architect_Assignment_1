// File: Server/src/main/java/edu/sdccd/cisc191/server/ClientHandler.java
package edu.sdccd.cisc191.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.common.Message;
import edu.sdccd.cisc191.common.SubTaskMessage;
import edu.sdccd.cisc191.common.Task;
import edu.sdccd.cisc191.common.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private UserManager userManager;
    private TaskManager taskManager;
    private DataStore dataStore;
    private ObjectMapper objectMapper;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, UserManager userManager, TaskManager taskManager, DataStore dataStore, ObjectMapper objectMapper) {
        this.socket = socket;
        this.userManager = userManager;
        this.taskManager = taskManager;
        this.dataStore = dataStore;
        this.objectMapper = objectMapper;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String receivedJson;
            while ((receivedJson = in.readLine()) != null) {
                handleClientMessage(receivedJson);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientMessage(String json) throws IOException {
        Message message = objectMapper.readValue(json, Message.class);

        switch (message.getAction()) {
            case "LOGIN":
                handleLogin(message);
                break;
            case "REGISTER":
                handleRegister(message);
                break;
            case "ADD_TASK":
                handleAddTask(message);
                break;
            case "GET_TASKS":
                handleGetTasks();
                break;
            case "ADD_SUB_TASK":
                handleAddSubTask(message);
                break;
            // Add more cases for editing and deleting tasks and sub-tasks
            default:
                sendResponse(new Message("ERROR", "Unknown action."));
        }
    }

    private void handleLogin(Message message) throws IOException {
        User user = objectMapper.convertValue(message.getData(), User.class);
        boolean authenticated = userManager.authenticate(user);
        if (authenticated) {
            sendResponse(new Message("LOGIN_RESPONSE", "SUCCESS"));
        } else {
            sendResponse(new Message("LOGIN_RESPONSE", "FAILURE"));
        }
    }

    private void handleRegister(Message message) throws IOException {
        User user = objectMapper.convertValue(message.getData(), User.class);
        boolean registered = userManager.register(user);
        if (registered) {
            dataStore.saveUsers(); // Persist changes
            sendResponse(new Message("REGISTER_RESPONSE", "SUCCESS"));
        } else {
            sendResponse(new Message("REGISTER_RESPONSE", "USER_EXISTS"));
        }
    }

    private void handleAddTask(Message message) throws IOException {
        Task task = objectMapper.convertValue(message.getData(), Task.class);
        taskManager.addTask(task);
        dataStore.saveTasks(); // Persist changes
        sendResponse(new Message("ADD_TASK_RESPONSE", "SUCCESS"));
    }

    private void handleGetTasks() throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendResponse(new Message("TASK_LIST", tasks));
    }

    private void handleAddSubTask(Message message) throws IOException {
        // Expecting data to be a SubTaskMessage containing parentDescription and subTask
        SubTaskMessage subTaskMessage = objectMapper.convertValue(message.getData(), SubTaskMessage.class);
        boolean success = taskManager.addSubTask(subTaskMessage.getParentDescription(), subTaskMessage.getSubTask());
        if (success) {
            dataStore.saveTasks(); // Persist changes
            sendResponse(new Message("ADD_SUB_TASK_RESPONSE", "SUCCESS"));
        } else {
            sendResponse(new Message("ADD_SUB_TASK_RESPONSE", "FAILURE: Parent task not found."));
        }
    }

    private void sendResponse(Message message) throws IOException {
        String responseJson = objectMapper.writeValueAsString(message);
        out.println(responseJson);
    }
}
