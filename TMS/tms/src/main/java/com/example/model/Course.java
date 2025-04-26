package com.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Objects;

/**
 * Course entity representing a course within a training program
 */
public class Course {
    private Long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private int duration; // in hours
    private String status; // DRAFT, ACTIVE, ARCHIVED
    private Long trainingProgramId;
    private Long assignedTrainerId;
    private int sequence;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int durationHours;
    private int orderNum;
    private Date startDate;
    private Date endDate;
    
    // For relationships
    private TrainingProgram trainingProgram;
    private User assignedTrainer;

    // Constructors
    public Course() {
    }

    public Course(Long id, String title, String description, String category, BigDecimal price, 
                 int duration, String status, Long trainingProgramId, Long assignedTrainerId, 
                 int sequence, Timestamp createdAt, Timestamp updatedAt, int durationHours,
                 int orderNum, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.duration = duration;
        this.status = status;
        this.trainingProgramId = trainingProgramId;
        this.assignedTrainerId = assignedTrainerId;
        this.sequence = sequence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.durationHours = durationHours;
        this.orderNum = orderNum;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(Long trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public Long getAssignedTrainerId() {
        return assignedTrainerId;
    }

    public void setAssignedTrainerId(Long assignedTrainerId) {
        this.assignedTrainerId = assignedTrainerId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TrainingProgram getTrainingProgram() {
        return trainingProgram;
    }

    public void setTrainingProgram(TrainingProgram trainingProgram) {
        this.trainingProgram = trainingProgram;
    }

    public User getAssignedTrainer() {
        return assignedTrainer;
    }

    public void setAssignedTrainer(User assignedTrainer) {
        this.assignedTrainer = assignedTrainer;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return durationHours == course.durationHours &&
               orderNum == course.orderNum &&
               Objects.equals(id, course.id) &&
               Objects.equals(title, course.title) &&
               Objects.equals(description, course.description) &&
               Objects.equals(trainingProgramId, course.trainingProgramId) &&
               Objects.equals(status, course.status) &&
               Objects.equals(assignedTrainerId, course.assignedTrainerId) &&
               Objects.equals(startDate, course.startDate) &&
               Objects.equals(endDate, course.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, trainingProgramId, status,
                           durationHours, assignedTrainerId, orderNum, startDate, endDate);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", trainingProgramId=" + trainingProgramId +
                ", durationHours=" + durationHours +
                ", assignedTrainerId=" + assignedTrainerId +
                ", orderNum=" + orderNum +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
} 