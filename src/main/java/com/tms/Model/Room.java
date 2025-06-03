package com.tms.Model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a physical room or space in the Training Management System.
 * Tracks capacity, location, and availability status of the room.
 */
public class Room {
    private int id;
    private int capacity;
    private String location;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for room status with proper naming convention
    public enum Status {
        AVAILABLE, 
        UNAVAILABLE, 
        MAINTENANCE;

        /**
         * Converts a string to Status enum (case-insensitive)
         * @param value The string value to convert
         * @return Corresponding Status or null if invalid
         */
        public static Status fromString(String value) {
            if (value == null) return null;
            try {
                return Status.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

            public static boolean isValid(String status) {
        try {
            valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    }

    // Constructors
    public Room() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Room(int id) {
        this();
        this.id = id;
    }

    public Room(int id, int capacity, String location, Status status) {
        this();
        this.id = id;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
    }

    public Room(int i, String string, Status available) {
        this.id = i;
        this.location = string;
        this.status = available;
    }

    // Getters and setters with validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.updatedAt = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        this.location = location.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    // Overloaded setter for String status input
    public void setStatus(String status) {
        this.status = Status.fromString(status);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business logic methods
    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }

    public boolean canAccommodate(int numberOfPeople) {
        return isAvailable() && numberOfPeople > 0 && numberOfPeople <= capacity;
    }

    // Standard methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && 
               Objects.equals(location, room.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }
}