package com.tms.Model;

import java.sql.Timestamp;

public class Feedback {
    private int id;
    private Session session;
    private User trainee;
    private String comment;
    private Timestamp submittedAt;
    private int rating;

    // Constructors
    public Feedback() {}

    public Feedback(Session session, User trainee, String comment, int rating) {
        this.session = session;
        this.trainee = trainee;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}

