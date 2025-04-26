package com.example.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Represents a training session in the system
 */
public class Session {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Date sessionDate;
    private Time startTime;
    private Time endTime;
    private Long trainerId;
    private String location;
    private String materials;
    private String sessionStatus; // SCHEDULED, COMPLETED, CANCELLED

    public Session() {
    }

    public Session(Long id, Long courseId, String title, String description, Date sessionDate, Time startTime, 
                  Time endTime, Long trainerId, String location, String materials, String sessionStatus) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.trainerId = trainerId;
        this.location = location;
        this.materials = materials;
        this.sessionStatus = sessionStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", sessionDate=" + sessionDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", trainerId=" + trainerId +
                ", location='" + location + '\'' +
                ", sessionStatus='" + sessionStatus + '\'' +
                '}';
    }
} 