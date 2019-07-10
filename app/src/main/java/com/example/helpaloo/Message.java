package com.example.helpaloo;

import java.util.Date;

public class Message {
    String message;
    String timestamp;
    String userIDTo;
    String userIDFrom;
    String nameFrom;
    String nameTo;

    public Message(String message, String timestamp, String userIDFrom, String userIDTo, String nameFrom, String nameTo) {
        this.message = message;
        this.timestamp = timestamp;
        this.userIDFrom = userIDFrom;
        this.userIDTo = userIDTo;
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
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

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userIDTo='" + userIDTo + '\'' +
                ", userIDFrom='" + userIDFrom + '\'' +
                ", nameFrom='" + nameFrom + '\'' +
                ", nameTo='" + nameTo + '\'' +
                '}';
    }
}
