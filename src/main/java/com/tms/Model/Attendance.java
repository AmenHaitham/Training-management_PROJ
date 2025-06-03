package com.tms.Model;

import java.sql.Timestamp;

public class Attendance {
    private int id;
    private User trainee;
    private Session session;
    private String status;
    private java.time.LocalDate attendanceDate;
    private Timestamp recordedAt;

    // Constructors
    public Attendance() {}

    public Attendance(User trainee, Session session, String status, java.time.LocalDate attendanceDate) {
        this.trainee = trainee;
        this.session = session;
        this.status = status;
        this.attendanceDate = attendanceDate;
    }

    

    // Getters and Setters
    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.time.LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(java.time.LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public Timestamp getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Timestamp recordedAt) { this.recordedAt = recordedAt; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
