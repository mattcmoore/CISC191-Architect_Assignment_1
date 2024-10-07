// File: server/UserManager.java
package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.common.User;

import java.util.List;

public class UserManager {
    private List<User> users;

    public UserManager(List<User> users) {
        this.users = users;
    }

    // Authenticate user credentials
    public synchronized boolean authenticate(User user) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()) &&
                        u.getPassword().equals(user.getPassword()));
    }

    // Register a new user
    public synchronized boolean register(User user) {
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (exists) {
            return false; // User already exists
        }
        users.add(user);
        return true;
    }

    // Additional user-related methods can be added here
}
