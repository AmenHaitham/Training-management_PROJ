package com.training.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Session {
    private Long id;
    private Course course;
    private String title;
    private String notes;
    private Date scheduledDatetime;
    private int duration; // in minutes
    private String location;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private List<Material> materials;
    private List<Attendance> attendances;

    // Constructors
    public Session() {
        this.materials = new ArrayList<>();
        this.attendances = new ArrayList<>();
    }

    public Session(Course course, String title, String notes, Date scheduledDatetime, 
                  int duration, String location) {
        this();
        this.course = course;
        this.title = title;
        this.notes = notes;
        this.scheduledDatetime = scheduledDatetime;
        this.duration = duration;
        this.location = location;
        this.status = "SCHEDULED";
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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getScheduledDatetime() { return scheduledDatetime; }
    public void setScheduledDatetime(Date scheduledDatetime) { this.scheduledDatetime = scheduledDatetime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<Material> getMaterials() { return materials; }
    public void setMaterials(List<Material> materials) { this.materials = materials; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    // Business Methods
    public boolean addMaterial(Material material) {
        return materials.add(material);
    }

    public boolean removeMaterial(Material material) {
        return materials.remove(material);
    }

    public boolean markAttendance(User trainee, boolean attended) {
        Attendance attendance = new Attendance(this, trainee, attended);
        return attendances.add(attendance);
    }

    public boolean isScheduled() {
        return "SCHEDULED".equals(status);
    }

    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public int getAttendanceCount() {
        return (int) attendances.stream().filter(Attendance::isAttended).count();
    }

    public List<User> getAttendedTrainees() {
        List<User> attendedTrainees = new ArrayList<>();
        for (Attendance attendance : attendances) {
            if (attendance.isAttended()) {
                attendedTrainees.add(attendance.getTrainee());
            }
        }
        return attendedTrainees;
    }

    public boolean hasStarted() {
        return scheduledDatetime.before(new Date());
    }

    public boolean hasEnded() {
        Date endTime = new Date(scheduledDatetime.getTime() + (duration * 60 * 1000));
        return endTime.before(new Date());
    }
} 