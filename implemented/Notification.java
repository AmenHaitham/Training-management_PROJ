package org.example.hana;
import java.time.LocalDateTime;
import java.util.Date;

public class Notification {
    private int notificationID;
    private int userID;
    private String message;
    private Date timestamp;
    private boolean isRead;

    public Notification(
            int notificationID, String message,
            int userID, boolean isRead, Date timestamp
    ) {
        this.message = message;
        this.isRead = isRead;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;}

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;}

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }


}