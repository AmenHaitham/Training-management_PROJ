package com.training.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Assessment {
    private Long id;
    private Course course;
    private String title;
    private String description;
    private int totalMarks;
    private int passingMarks;
    private Date dueDate;
    private User createdBy;
    private Date createdAt;
    private Date updatedAt;
    private List<AssessmentResult> results;

    // Constructors
    public Assessment() {
        this.results = new ArrayList<>();
    }

    public Assessment(Course course, String title, String description, int totalMarks, 
                     int passingMarks, Date dueDate, User createdBy) {
        this();
        this.course = course;
        this.title = title;
        this.description = description;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<AssessmentResult> getResults() { return results; }
    public void setResults(List<AssessmentResult> results) { this.results = results; }

    // Business Methods
    public boolean addResult(AssessmentResult result) {
        return results.add(result);
    }

    public boolean removeResult(AssessmentResult result) {
        return results.remove(result);
    }

    public AssessmentResult getResultForTrainee(User trainee) {
        for (AssessmentResult result : results) {
            if (result.getTrainee().equals(trainee)) {
                return result;
            }
        }
        return null;
    }

    public boolean isPastDue() {
        return dueDate.before(new Date());
    }

    public double getAverageScore() {
        if (results.isEmpty()) return 0.0;
        
        int totalScore = 0;
        for (AssessmentResult result : results) {
            totalScore += result.getMarksObtained();
        }
        return (double) totalScore / results.size();
    }

    public int getPassedCount() {
        int count = 0;
        for (AssessmentResult result : results) {
            if (result.getMarksObtained() >= passingMarks) {
                count++;
            }
        }
        return count;
    }

    public int getFailedCount() {
        return results.size() - getPassedCount();
    }

    public double getPassingRate() {
        if (results.isEmpty()) return 0.0;
        return (double) getPassedCount() / results.size() * 100;
    }

    public List<AssessmentResult> getTopPerformers(int limit) {
        List<AssessmentResult> sortedResults = new ArrayList<>(results);
        sortedResults.sort((r1, r2) -> r2.getMarksObtained() - r1.getMarksObtained());
        return sortedResults.subList(0, Math.min(limit, sortedResults.size()));
    }
} 