package com.example.model;

import java.time.LocalDateTime;

public class AssessmentResult {
    private Long id;
    private Long assessmentId;
    private Long traineeId;
    private Integer marksObtained;
    private LocalDateTime submittedAt;
    private Long gradedBy;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For relationships
    private Assessment assessment;
    private User trainee;
    private User grader;

    // Constructors
    public AssessmentResult() {
    }

    public AssessmentResult(Long id, Long assessmentId, Long traineeId, Integer marksObtained,
                           LocalDateTime submittedAt, Long gradedBy, String feedback,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.traineeId = traineeId;
        this.marksObtained = marksObtained;
        this.submittedAt = submittedAt;
        this.gradedBy = gradedBy;
        this.feedback = feedback;
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

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    public Integer getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Integer marksObtained) {
        this.marksObtained = marksObtained;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(Long gradedBy) {
        this.gradedBy = gradedBy;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public User getGrader() {
        return grader;
    }

    public void setGrader(User grader) {
        this.grader = grader;
    }

    @Override
    public String toString() {
        return "AssessmentResult{" +
                "id=" + id +
                ", assessmentId=" + assessmentId +
                ", traineeId=" + traineeId +
                ", marksObtained=" + marksObtained +
                ", submittedAt=" + submittedAt +
                ", gradedBy=" + gradedBy +
                '}';
    }
} 