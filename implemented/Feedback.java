package org.example.hana;
import java.sql.CallableStatement;
import java.util.Date;
public class Feedback {
    private int feedbackID;
    private int userID;
    private int rating;
    private Date submittedat;
    private String comments;

    public Feedback(int feedbackID, int userID,
                    int rating, String comments, Date submittedat) {
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.submittedat = submittedat;
        this.comments = comments;
        this.rating = rating;

    }

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;}

    public int getUserID() {
        return userID;}

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getSubmittedAt() {
        return submittedat;}

    public void setSubmittedat(Date submittedat) {
        this.submittedat = submittedat;}

    public String getComments() {return comments;}

    public void setComments(String comments) {
        this.comments = comments;
    }


}

