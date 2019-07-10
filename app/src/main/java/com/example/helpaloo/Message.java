package com.example.helpaloo;

import java.util.Date;

public class Message {
    String message;
    String timestamp;
    String userIDTo;
    String userIDFrom;
    String email;
    String emailTo;

    public Message(String message, String timestamp, String userIDFrom, String userIDTo, String email, String emailTo) {
        this.message = message;
        this.timestamp = timestamp;
        this.userIDFrom = userIDFrom;
        this.userIDTo = userIDTo;
        this.email = email;
        this.emailTo = emailTo;
    }

    public Message(){
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userIDFrom;
    }

    public void setUserID(String userID) {
        this.userIDFrom = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getUserIDTo() {
        return userIDTo;
    }

    public void setUserIDTo(String userIDTo) {
        this.userIDTo = userIDTo;
    }

    public String getUserIDFrom() {
        return userIDFrom;
    }

    public void setUserIDFrom(String userIDFrom) {
        this.userIDFrom = userIDFrom;
    }
}
