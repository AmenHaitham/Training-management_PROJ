package com.tms.Model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a course in the Training Management System.
 * Contains course details and the assigned trainer.
 */
public class Course {
    private int id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User trainer;
    

    // Constructors
    public Course() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Course(int id, String title, String description, User trainer) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.trainer = trainer;
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
            throw new IllegalArgumentException("Course title cannot be null or empty");
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
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        if (trainer == null || trainer.getRole() != User.Role.TRAINER) {
            throw new IllegalArgumentException("Trainer must be a user with TRAINER role");
        }
        this.trainer = trainer;
        this.updatedAt = LocalDateTime.now();
    }


    // Standard methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && 
               Objects.equals(title, course.title) && 
               Objects.equals(trainer, course.trainer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, trainer);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", trainer=" + (trainer != null ? trainer.getFullName() : "None") +
                '}';
    }
}