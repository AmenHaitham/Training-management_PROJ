package com.tms.Model;

import java.time.LocalDate;

import java.util.Objects;

/**
 * Represents the relationship between a Training and a Course in the system.
 * Tracks session information and scheduling details.
 */
public class TrainingCourse {
    private int id;
    private Training training;
    private Course course;
    private int totalSessions;
    private int currentSessions;
    private int completedSessions;
    private int cancelledSessions;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    public enum Status {
        COMPLETED, CANCELLED, COMING, LIVE;

        public static Status fromString(String value) {
            if (value == null) return null;
            try {
                return Status.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public TrainingCourse() {}

    public TrainingCourse(int id, Training training, Course course, int totalSessions,
                         int currentSessions, int completedSessions,
                         int cancelledSessions, LocalDate startDate, LocalDate endDate,
                         Status status) {
        this();
        this.id = id;
        this.training = training;
        this.course = course;
        this.totalSessions = totalSessions;
        this.currentSessions = currentSessions;
        this.completedSessions = completedSessions;
        this.cancelledSessions = cancelledSessions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters with validation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }
        this.training = training;
        
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        this.course = course;
        
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        if (totalSessions <= 0) {
            throw new IllegalArgumentException("Total sessions must be positive");
        }
        this.totalSessions = totalSessions;
        
        updateCurrentSessions();
    }

    public int getCurrentSessions() {
        return currentSessions;
    }

    public void setCurrentSessions(int currentSessions) {
        if (currentSessions < 0 || currentSessions > totalSessions) {
            throw new IllegalArgumentException("Current sessions must be between 0 and total sessions");
        }
        this.currentSessions = currentSessions;
        
        updateStatus();
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        if (completedSessions < 0 || completedSessions > totalSessions) {
            throw new IllegalArgumentException("Completed sessions must be between 0 and total sessions");
        }
        this.completedSessions = completedSessions;
        
        updateCurrentSessions();
        updateStatus();
    }

    public int getCancelledSessions() {
        return cancelledSessions;
    }

    public void setCancelledSessions(int cancelledSessions) {
        if (cancelledSessions < 0 || cancelledSessions > totalSessions) {
            throw new IllegalArgumentException("Cancelled sessions must be between 0 and total sessions");
        }
        this.cancelledSessions = cancelledSessions;
        
        updateCurrentSessions();
        updateStatus();
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
        
        updateStatus();
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
        
        updateStatus();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
     
    }

    // Overloaded setter for String status
    public void setStatus(String status) {
        this.status = Status.fromString(status);
     
    }


    // Business logic methods
    private void updateCurrentSessions() {
        this.currentSessions = totalSessions - completedSessions - cancelledSessions;
    }

    private void updateStatus() {
    // Get current date at the start to ensure consistency
    LocalDate currentDate = LocalDate.now();
    
    // Handle null cases first
    if (startDate == null || endDate == null) {
        this.status = Status.COMING; // Default status if dates aren't set
        return;
    }

    // Check session completion states
    if (totalSessions > 0) {
        if (totalSessions == completedSessions) {
            this.status = Status.COMPLETED;
            return;
        }
        if (totalSessions == cancelledSessions) {
            this.status = Status.CANCELLED;
            return;
        }
    }

    // Check date-based statuses
    if (currentDate.isAfter(endDate)) {
        this.status = Status.COMPLETED;
    } else if (currentDate.isBefore(startDate)) {
        this.status = Status.COMING;
    } else {
        // If we're between start and end dates
        this.status = Status.LIVE;
    }
}

    public boolean isActive() {
        return status == Status.COMING || status == Status.LIVE;
    }

    public double getCompletionPercentage() {
        if (totalSessions == 0) return 0;
        return (completedSessions * 100.0) / totalSessions;
    }

    // Standard methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingCourse that = (TrainingCourse) o;
        return id == that.id && 
               Objects.equals(training, that.training) && 
               Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, training, course);
    }

    @Override
    public String toString() {
        return "TrainingCourse{" +
                "id=" + id +
                ", training=" + (training != null ? training.getId() : "null") +
                ", course=" + (course != null ? course.getId() : "null") +
                ", status=" + status +
                ", completion=" + String.format("%.1f%%", getCompletionPercentage()) +
                ", dates=" + startDate + " to " + endDate +
                '}';
    }
}