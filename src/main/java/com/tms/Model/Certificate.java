package com.tms.Model;

import java.sql.Timestamp;

public class Certificate {
    private int id;
    private User trainee;
    private Training training;
    private Timestamp issuedAt;
    private byte[] certificateFile;

    // Constructors
    public Certificate() {}

    public Certificate(User trainee, Training training, byte[] certificateFile) {
        this.trainee = trainee;
        this.training = training;
        this.certificateFile = certificateFile;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getTrainee() { return trainee; }
    public void setTrainee(User trainee) { this.trainee = trainee; }

    public Training getTraining() { return training; }
    public void setTraining(Training training) { this.training = training; }

    public Timestamp getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Timestamp issuedAt) { this.issuedAt = issuedAt; }

    public byte[] getCertificateFile() { return this.certificateFile; }
    public void setCertificateFile(byte[] certificateFile) { this.certificateFile = certificateFile; }
}
