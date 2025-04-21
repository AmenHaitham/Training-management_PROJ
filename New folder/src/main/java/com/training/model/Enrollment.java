package com.training.model;

import java.util.Date;

public class Enrollment {
    private Long id;
    private User trainee;
    private Course course;
    private TrainingProgram trainingProgram;
    private Date enrollmentDate;
    private Date completionDate;
    private String status; // ACTIVE, COMPLETED, DROPPED
    private double progress; // percentage of completion
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Enrollment() {}

    public Enrollment(Long id, User trainee, Course course) {
        this.id = id;
        this.trainee = trainee;
        this.course = course;
        this.enrollmentDate = new Date();
        this.status = "ACTIVE";
        this.progress = 0.0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Enrollment(User trainee, TrainingProgram trainingProgram) {
        this.trainee = trainee;
        this.trainingProgram = trainingProgram;
        this.enrollmentDate = new Date();
        this.status = "ACTIVE";
        this.progress = 0.0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public TrainingProgram getTrainingProgram() { return trainingProgram; }
    public void setTrainingProgram(TrainingProgram trainingProgram) { this.trainingProgram = trainingProgram; }
    
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
} 