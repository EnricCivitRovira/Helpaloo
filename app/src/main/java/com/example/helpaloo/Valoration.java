package com.example.helpaloo;

import androidx.annotation.NonNull;

public class Valoration {

    private String comment;
    private float starRate;
    private String userID;

    Valoration(){};

    Valoration(String comment, float starRate, String userID){
        this.comment = comment;
        this.starRate = starRate;
        this.userID = userID;
    }

    String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getStarRate() {
        return starRate;
    }

    public void setStarRate(float starRate) {
        this.starRate = starRate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @NonNull
    @Override
    public String toString() {
        return "Valoration{" +
                "comment='" + comment + '\'' +
                ", starRate=" + starRate +
                ", userID='" + userID + '\'' +
                '}';
    }
}
