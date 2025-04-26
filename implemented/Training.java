package org.example.hana;
import java.sql.CallableStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
public class Training {
    private int trainingID;
    private String title;
    private java.util.Date date;
    private String description;
    private int createdBy;
    private String status;
    private ArrayList<Course> courses;
    private ArrayList<Trainee> enrolledTrainee;

    public Training(int trainingID, String title,
                    String description, int createdBy,
                    String status) {
        this.trainingID = trainingID;
        this.description = description;
        this.title = title;
        this.status = status;
        this.courses = new ArrayList<>();
        this.enrolledTrainee = new ArrayList<>();
    }

    public java.util.Date getDate() {
        return date;
    }
    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public int getTrainingID() {
        return trainingID;
    }

    public void setTrainingID
            (int trainingID)
    {
        this.trainingID = trainingID;
    }

    public String getTitle()
    {
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus
            (String status) {
        this.status = status;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses
            (ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<Trainee> getEnrolledTrainee() {
        return enrolledTrainee;
    }

    public void setEnrolledTrainee(ArrayList<Trainee> enrolledTrainee) {
        this.enrolledTrainee = enrolledTrainee;
    }

}
