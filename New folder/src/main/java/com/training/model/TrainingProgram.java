package com.training.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class TrainingProgram {
    private Long id;
    private String title;
    private String description;
    private User createdBy;
    private String status;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;
    private List<Course> courses;
    private List<Enrollment> enrollments;

    // Constructors
    public TrainingProgram() {
        this.courses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
    }

    public TrainingProgram(String title, String description, User createdBy, 
                          String status, Date startDate, Date endDate) {
        this();
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    // Business Methods
    public boolean addCourse(Course course) {
        return courses.add(course);
    }

    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    public boolean enrollTrainee(User trainee) {
        if (trainee.getRole().equals("TRAINEE")) {
            Enrollment enrollment = new Enrollment(trainee, this);
            return enrollments.add(enrollment);
        }
        return false;
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public int getEnrolledTraineesCount() {
        return enrollments.size();
    }

    public List<User> getEnrolledTrainees() {
        List<User> trainees = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            trainees.add(enrollment.getTrainee());
        }
        return trainees;
    }
} 