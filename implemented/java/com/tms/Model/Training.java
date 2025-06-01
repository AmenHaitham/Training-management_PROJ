package com.tms.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a training program in the Training Management System.
 * Includes information about the training schedule, location, and status.
 */
public class Training {
    private int id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private int progress;

    // Enum for status to enforce type safety
    public enum Status {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    CANCELED("Canceled"),
    COMPLETED("Completed"),
    LIVE("Live");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Status fromString(String value) {
        if (value == null) return null;
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}


    // Constructors
    public Training() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Training(int id, String title, String description, LocalDate startDate, 
                   LocalDate endDate, Status status, Room room) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.room = room;
    }

    

    // Getters and setters with validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.updatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    // Overloaded setter for String status input
    public void setStatus(String status) {
        Status parsedStatus = Status.fromString(status);
        if (parsedStatus == null) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
        setStatus(parsedStatus);
    }

    // Business logic methods
    public boolean isActive() {
        return status == Status.AVAILABLE || status == Status.LIVE;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isUpcoming() {
        LocalDate today = LocalDate.now();
        return startDate != null && startDate.isAfter(today) && isActive();
    }

    public boolean isOngoing() {
        LocalDate today = LocalDate.now();
        return startDate != null && endDate != null && 
               !today.isBefore(startDate) && !today.isAfter(endDate) && 
               (status == Status.LIVE || status == Status.AVAILABLE);
    }

    // Utility methods
    public String getDuration() {
        if (startDate == null || endDate == null) return "Not scheduled";
        return startDate.toString() + " to " + endDate.toString();
    }

    // Standard methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return id == training.id && 
               Objects.equals(title, training.title) && 
               Objects.equals(startDate, training.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, startDate);
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", room=" + (room != null ? room.getId() : "null") +
                '}';
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}