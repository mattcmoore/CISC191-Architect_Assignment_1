// File: server/DataStore.java
package edu.sdccd.cisc191.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.common.Task;
import edu.sdccd.cisc191.common.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String USERS_FILE = "users.json";
    private static final String TASKS_FILE = "tasks.json";

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<User> users = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    public DataStore() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // For Java 8 Date/Time
        loadUsers();
        loadTasks();
    }

    // Load users from file or initialize with a default admin user
    public void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try {
                users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Initialize with a default admin user
            users.add(new User("admin", "admin"));
            saveUsers();
        }
    }

    // Save users to file
    public void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load tasks from file or initialize an empty task list
    public void loadTasks() {
        File file = new File(TASKS_FILE);
        if (file.exists()) {
            try {
                tasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Initialize with an empty task list
            saveTasks();
        }
    }

    // Save tasks to file
    public void saveTasks() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(TASKS_FILE), tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public List<User> getUsers() {
        return users;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
