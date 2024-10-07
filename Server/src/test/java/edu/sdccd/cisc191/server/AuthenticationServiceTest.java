package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.common.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user1", "password1"));
        users.add(new User("user2", "password2"));
        authService = new AuthenticationService(users);
    }

    @Test
    void testAuthenticateUser_Success() {
        User validUser = new User("user1", "password1");
        assertTrue(authService.authenticateUser(validUser));
    }

    @Test
    void testAuthenticateUser_Failure() {
        User invalidUser = new User("user3", "wrongpassword");
        assertFalse(authService.authenticateUser(invalidUser));
    }
}
