package com.example.aryobee.UsersAccountManagment;

public class User {

    String uID;
    String name;
    String email;
    Ratings ratings;
    int status;
    String FCMID;

    public User(){}

    public User(String uID, String name, String email, Ratings ratings, int status, String FCMID) {
        this.uID = uID;
        this.name = name;
        this.email = email;
        this.ratings = ratings;
        this.status = status;
        this.FCMID = FCMID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getuID() {

        return uID;
    }

    public void setuID(String uID) {

        this.uID = uID;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getFCMID() {
        return FCMID;
    }

    public void setFCMID(String FCMID) {
        this.FCMID = FCMID;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }
}


