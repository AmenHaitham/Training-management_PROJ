package com.tms.Model;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;

public class Session {
    
    private int id;
    private TrainingCourse trainingCourseId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sessionDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time endTime;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String title;
    private String location;


    public TrainingCourse getTrainingCourseId() {
        return trainingCourseId;
    }

    public void setTrainingCourseId(TrainingCourse trainingCourseId) {
        this.trainingCourseId = trainingCourseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Constructors
    public Session() {}

    public Session(TrainingCourse trainingCourseId, LocalDate sessionDate, Time startTime, Time endTime, Status status) {
        this.trainingCourseId = trainingCourseId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public TrainingCourse getTrainingCourse() { return trainingCourseId; }
    public void setTrainingCourse(TrainingCourse trainingCourseId) { this.trainingCourseId = trainingCourseId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public enum Status {
        COMPLETED,
        CANCELLED,
        COMING,
    }

}
