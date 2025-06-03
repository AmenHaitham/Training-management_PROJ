package com.tms.Model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a user in the Training Management System with enhanced security and validation.
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean status;
    private byte[] photo;
    private Gender gender;
    private String address;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    // Enum for gender with enhanced validation
    public enum Gender {
        MALE, FEMALE, OTHER;

        public static Gender fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return Gender.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender value: " + value + 
                    ". Valid values are: " + Arrays.toString(values()));
            }
        }
    }

    // Enum for user roles with enhanced validation
    public enum Role {
        ADMIN, TRAINER, TRAINEE;

        public static Role fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return Role.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role value: " + value + 
                    ". Valid values are: " + Arrays.toString(values()));
            }
        }
    }

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = true; // Default to active
    }

    public User(int id, String firstName, String lastName, String phoneNumber, 
               String email, String password, Gender gender, String address, 
               Role role) {
        this();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.address = address;
        this.role = role;
    }

    public User(int int1, String string, String string2, String string3, String string4, boolean boolean1,
            Gender valueOf, String string5, Role valueOf2, byte[] bytes, LocalDateTime localDateTime, Object object) {
        //TODO Auto-generated constructor stub
    }

    // Getters and setters with enhanced validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID cannot be negative");
        }
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (!NAME_PATTERN.matcher(firstName).matches()) {
            throw new IllegalArgumentException("First name contains invalid characters");
        }
        this.firstName = firstName.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (!NAME_PATTERN.matcher(lastName).matches()) {
            throw new IllegalArgumentException("Last name contains invalid characters");
        }
        this.lastName = lastName.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phoneNumber = phoneNumber.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim().toLowerCase();
        this.updatedAt = LocalDateTime.now();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public byte[] getPhoto() {
        return photo != null ? photo.clone() : null;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo != null ? photo.clone() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = Objects.requireNonNull(gender, "Gender cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void setGender(String gender) {
        this.gender = Gender.fromString(gender);
        this.updatedAt = LocalDateTime.now();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.address = address.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void setRole(String role) {
        this.role = Role.fromString(role);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date cannot be null");
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update date cannot be null");
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Security consideration: Clear sensitive data from memory
    public void clearSensitiveData() {
        if (password != null) {
            password = null;
        }
    }

    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && 
               Objects.equals(email, user.email) && 
               Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Factory method for creating a new user
    public static User createNewUser(String firstName, String lastName, 
                                   String phoneNumber, String email, 
                                   String password, Gender gender, 
                                   String address, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(password);
        user.setGender(gender);
        user.setAddress(address);
        user.setRole(role);
        return user;
    }
}