// File: Common/src/main/java/edu/sdccd/cisc191/common/User.java
package edu.sdccd.cisc191.common;

public class User {
    private String username;
    private String password;

    public User() {
        // Default constructor for Jackson
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
