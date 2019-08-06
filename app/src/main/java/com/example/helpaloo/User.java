package com.example.helpaloo;

import androidx.annotation.NonNull;

public class User {

        private String userID;
        private String email;
        private String name;
        private String surname;
        private Double latitude;
        private Double longitude;
        private String route;
        private int distanceToShowPosts;
        private float mediumValoration;
        private int nValorations;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userID, String email, String name, String surname, Double latitude, Double longitude, String route, int distanceToShowPosts, float mediumValoration, int nValorations) {
            this.userID = userID;
            this.email = email;
            this.name = name;
            this.surname = surname;
            this.longitude = longitude;
            this.latitude = latitude;
            this.route = route;
            if(distanceToShowPosts == -1) {
                this.distanceToShowPosts = 50;
            }else {
                this.distanceToShowPosts = distanceToShowPosts;
            }
            this.mediumValoration = mediumValoration;
            this.nValorations = nValorations;
        };

    int getDistanceToShowPosts() {
        return distanceToShowPosts;
    }

    public void setDistanceToShowPosts(int distanceToShowPosts) {
        this.distanceToShowPosts = distanceToShowPosts;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    float getMediumValoration() {
        return mediumValoration;
    }

    public void setMediumValoration(float mediumValoration) {
        this.mediumValoration = mediumValoration;
    }

    int getnValorations() {
        return nValorations;
    }

    public void setnValorations(int nValorations) {
        this.nValorations = nValorations;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", route='" + route + '\'' +
                ", distanceToShowPosts=" + distanceToShowPosts +
                ", mediumValoration=" + mediumValoration +
                ", nValorations=" + nValorations +
                '}';
    }
}
