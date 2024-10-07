// File: server/AuthenticationService.java
package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.common.User;

import java.util.List;

public class AuthenticationService {
    private List<User> users;

    public AuthenticationService(List<User> users) {
        this.users = users;
    }

    // Authenticate user credentials
    public boolean authenticateUser(User user) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()) &&
                        u.getPassword().equals(user.getPassword()));
    }

    // Register a new user
    public boolean registerUser(User user) {
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (exists) {
            return false; // User already exists
        }
        users.add(user);
        return true;
    }
}
