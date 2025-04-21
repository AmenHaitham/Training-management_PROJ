package com.training.model;

import java.util.Date;

public class AssessmentResult {
    private Long id;
    private Assessment assessment;
    private User trainee;
    private int marksObtained;
    private String feedback;
    private Date submissionDate;
    private String status; // PENDING, SUBMITTED, GRADED
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public AssessmentResult() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "PENDING";
    }

    public AssessmentResult(Assessment assessment, User trainee) {
        this();
        this.assessment = assessment;
        this.trainee = trainee;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assessment getAssessment() { return assessment; }
    public void setAssessment(Assessment assessment) { this.assessment = assessment; }

    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Business Methods
    public boolean isPassed() {
        return marksObtained >= assessment.getPassingMarks();
    }

    public double getPercentageScore() {
        return (double) marksObtained / assessment.getTotalMarks() * 100;
    }

    public boolean isSubmitted() {
        return "SUBMITTED".equals(status) || "GRADED".equals(status);
    }

    public boolean isGraded() {
        return "GRADED".equals(status);
    }

    public boolean isLateSubmission() {
        return submissionDate != null && submissionDate.after(assessment.getDueDate());
    }

    public void submit() {
        if (!"PENDING".equals(status)) {
            throw new IllegalStateException("Assessment result has already been submitted");
        }
        this.submissionDate = new Date();
        this.status = "SUBMITTED";
        this.updatedAt = new Date();
    }

    public void grade(int marks, String feedback) {
        if (!"SUBMITTED".equals(status)) {
            throw new IllegalStateException("Cannot grade an assessment that hasn't been submitted");
        }
        if (marks < 0 || marks > assessment.getTotalMarks()) {
            throw new IllegalArgumentException("Invalid marks");
        }
        this.marksObtained = marks;
        this.feedback = feedback;
        this.status = "GRADED";
        this.updatedAt = new Date();
    }
} 