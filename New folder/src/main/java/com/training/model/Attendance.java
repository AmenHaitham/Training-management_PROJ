package com.training.model;

import java.util.Date;

public class Attendance {
    private Long id;
    private Session session;
    private User trainee;
    private boolean attended;
    private Date checkInTime;
    private Date checkOutTime;
    private String notes;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Attendance() {}

    public Attendance(Session session, User trainee, boolean attended) {
        this.session = session;
        this.trainee = trainee;
        this.attended = attended;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public boolean isAttended() { return attended; }
    public void setAttended(boolean attended) { this.attended = attended; }

    public Date getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Date checkInTime) { this.checkInTime = checkInTime; }

    public Date getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Date checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Business Methods
    public void checkIn() {
        if (!attended) {
            this.checkInTime = new Date();
            this.attended = true;
            this.updatedAt = new Date();
        }
    }

    public void checkOut() {
        if (attended && checkInTime != null) {
            this.checkOutTime = new Date();
            this.updatedAt = new Date();
        }
    }

    public long getDurationInMinutes() {
        if (checkInTime != null && checkOutTime != null) {
            return (checkOutTime.getTime() - checkInTime.getTime()) / (60 * 1000);
        }
        return 0;
    }

    public boolean isLate() {
        if (checkInTime != null && session != null) {
            return checkInTime.after(session.getScheduledDatetime());
        }
        return false;
    }

    public boolean isEarlyLeave() {
        if (checkOutTime != null && session != null) {
            Date sessionEndTime = new Date(session.getScheduledDatetime().getTime() + 
                                        (session.getDuration() * 60 * 1000));
            return checkOutTime.before(sessionEndTime);
        }
        return false;
    }
} 