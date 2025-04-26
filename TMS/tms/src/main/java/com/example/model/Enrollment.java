package com.example.model;

import java.time.LocalDateTime;

public class Enrollment {
    private Long id;
    private Long traineeId;
    private Long trainingProgramId;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For relationships
    private User trainee;
    private TrainingProgram trainingProgram;

    // Constructors
    public Enrollment() {
    }

    public Enrollment(Long id, Long traineeId, Long trainingProgramId, String status, 
                     LocalDateTime enrolledAt, LocalDateTime completedAt, String notes,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainingProgramId = trainingProgramId;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Long getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(Long trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public TrainingProgram getTrainingProgram() {
        return trainingProgram;
    }

    public void setTrainingProgram(TrainingProgram trainingProgram) {
        this.trainingProgram = trainingProgram;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", traineeId=" + traineeId +
                ", trainingProgramId=" + trainingProgramId +
                ", status='" + status + '\'' +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
} 