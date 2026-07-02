package com.gabriel.twoforms.services;

import com.gabriel.twoforms.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private final String baseUrl = "http://localhost:8080/api/auth";
    private final RestTemplate restTemplate;
    private User currentUser;

    private AuthService() {
        restTemplate = new RestTemplate();
        // Register JavaTimeModule to handle Java 8 date/time deserialization
        try {
            for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
                if (restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter) {
                    MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters().get(i);
                    ObjectMapper mapper = converter.getObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                }
            }
        } catch (Throwable t) {
            System.err.println("Could not register JavaTimeModule in AuthService: " + t.getMessage());
        }
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        try {
            String url = baseUrl + "/login?username=" + username + "&password=" + password;
            ResponseEntity<User> response = restTemplate.postForEntity(url, null, User.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                currentUser = response.getBody();
                return true;
            }
        } catch (Exception ex) {
            System.err.println("Login failed: " + ex.getMessage());
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
        try {
            String url = baseUrl + "/users";
            ResponseEntity<Map<String, User>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, User>>() {}
            );
            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (Exception ex) {
            System.err.println("Failed to fetch users: " + ex.getMessage());
            return new HashMap<>();
        }
    }

    public void createUser(User user) {
        try {
            String url = baseUrl + "/users";
            restTemplate.postForEntity(url, user, User.class);
        } catch (Exception ex) {
            System.err.println("Failed to create user: " + ex.getMessage());
        }
    }

    public boolean updateUser(String userId, String newUsername, String newPassword, String newFullName) {
        try {
            String url = baseUrl + "/users/" + userId;
            User payload = new User();
            payload.setUsername(newUsername);
            payload.setPassword(newPassword);
            payload.setFullName(newFullName);
            restTemplate.put(url, payload);
            return true;
        } catch (Exception ex) {
            System.err.println("Failed to update user: " + ex.getMessage());
            return false;
        }
    }

    public void deleteUser(String username) {
        try {
            String url = baseUrl + "/users/" + username;
            restTemplate.delete(url);
        } catch (Exception ex) {
            System.err.println("Failed to delete user: " + ex.getMessage());
        }
    }
}
