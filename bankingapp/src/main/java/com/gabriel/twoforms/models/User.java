package com.gabriel.twoforms.models;

public class User {
    private String id;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private String phone;

    public enum Role {
        ADMIN, CUSTOMER
    }

    public User(String id, String username, String password, Role role, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
