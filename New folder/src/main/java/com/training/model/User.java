package com.training.model;

import java.util.Date;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role; // ADMIN, TRAINER, TRAINEE
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public User() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.active = true;
    }

    public User(String username, String email, String password, String firstName, String lastName, String role) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isTrainer() {
        return "TRAINER".equals(role);
    }

    public boolean isTrainee() {
        return "TRAINEE".equals(role);
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = new Date();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = new Date();
    }
} 