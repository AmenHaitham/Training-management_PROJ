package com.tms.Model;

public class TrainingProgress {
    private int id;
    private String title;
    private int progress;
    private String dates;
    private int sessionsCompleted;
    private int totalSessions;
    private User trainer;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public String getDates() {
        return dates;
    }
    public void setDates(String dates) {
        this.dates = dates;
    }
    public int getSessionsCompleted() {
        return sessionsCompleted;
    }
    public void setSessionsCompleted(int sessionsCompleted) {
        this.sessionsCompleted = sessionsCompleted;
    }
    public int getTotalSessions() {
        return totalSessions;
    }
    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }
    public User getTrainer() {
        return trainer;
    }
    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    // getters and setters

    
}