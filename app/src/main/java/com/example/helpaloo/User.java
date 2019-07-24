package com.example.helpaloo;

public class User {

        public String userID;
        public String email;
        public String name;
        public String surname;
        public Double latitude;
        public Double longitude;
        public String route;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userID, String email, String name, String surname, Double latitude, Double longitude, String route) {
            this.userID = userID;
            this.email = email;
            this.name = name;
            this.surname = surname;
            this.longitude = longitude;
            this.latitude = latitude;
            this.route = route;
        }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return userID;
    }

    public void setUsername(String username) {
        this.userID = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfilePicRoute() {
        return route;
    }

    public void setProfilePicRoute(String profilePicRoute) {
        this.route = profilePicRoute;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", profilePicRoute='" + route + '\'' +
                '}';
    }
}
