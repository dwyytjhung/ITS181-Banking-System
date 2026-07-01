package com.gabriel.twoforms.services;

import com.gabriel.twoforms.models.User;
import com.gabriel.twoforms.models.User.Role;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private final Map<String, User> users;
    private User currentUser;

    private AuthService() {
        users = new HashMap<>();
        // Seed some data
        users.put("admin", new User("U1", "admin", "admin123", Role.ADMIN, "System Administrator"));
        users.put("customer1", new User("U2", "customer1", "pass123", Role.CUSTOMER, "John Doe"));
        users.put("customer2", new User("U3", "customer2", "pass123", Role.CUSTOMER, "Jane Smith"));
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Map<String, User> getAllUsers() {
        return users;
    }

    public void createUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }
}
