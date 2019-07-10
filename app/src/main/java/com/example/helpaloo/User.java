package com.example.helpaloo;

public class User {

        public String userID;
        public String email;
        public String name;
        public String surname;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userID, String email, String name, String surname) {
            this.userID = userID;
            this.email = email;
            this.name = name;
            this.surname = surname;
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
}
