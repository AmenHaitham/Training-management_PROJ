package com.training.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Course {
    private Long id;
    private TrainingProgram trainingProgram;
    private String title;
    private String description;
    private String category;
    private int duration; // in hours
    private double price;
    private User trainer;
    private Date startDate;
    private Date endDate;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private List<Session> sessions;
    private List<Assessment> assessments;

    // Constructors
    public Course() {
        this.sessions = new ArrayList<>();
        this.assessments = new ArrayList<>();
    }

    public Course(TrainingProgram trainingProgram, String title, String description, 
                 String category, int duration, double price, User trainer) {
        this();
        this.trainingProgram = trainingProgram;
        this.title = title;
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.price = price;
        this.trainer = trainer;
        this.status = "DRAFT";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TrainingProgram getTrainingProgram() { return trainingProgram; }
    public void setTrainingProgram(TrainingProgram trainingProgram) { this.trainingProgram = trainingProgram; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public User getTrainer() { return trainer; }
    public void setTrainer(User trainer) { this.trainer = trainer; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public List<Assessment> getAssessments() { return assessments; }
    public void setAssessments(List<Assessment> assessments) { this.assessments = assessments; }

    // Business Methods
    public boolean addSession(Session session) {
        return sessions.add(session);
    }

    public boolean removeSession(Session session) {
        return sessions.remove(session);
    }

    public boolean addAssessment(Assessment assessment) {
        return assessments.add(assessment);
    }

    public boolean removeAssessment(Assessment assessment) {
        return assessments.remove(assessment);
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public int getTotalSessionsCount() {
        return sessions.size();
    }

    public int getTotalAssessmentsCount() {
        return assessments.size();
    }

    public List<Session> getUpcomingSessions() {
        List<Session> upcomingSessions = new ArrayList<>();
        Date now = new Date();
        for (Session session : sessions) {
            if (session.getScheduledDatetime().after(now)) {
                upcomingSessions.add(session);
            }
        }
        return upcomingSessions;
    }
} 