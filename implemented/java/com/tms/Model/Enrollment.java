package com.tms.Model;

import java.sql.Timestamp;

public class Enrollment {
    private int id;
    private User trainee;
    private Training training;
    private Timestamp enrollmentDate;

    // Constructors
    public Enrollment() {}

    public Enrollment(User trainee, Training training) {
        this.trainee = trainee;
        this.training = training;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public Training getTraining() { return training; }
    public void setTraining(Training training) { this.training = training; }

    public Timestamp getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Timestamp enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
