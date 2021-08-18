package com.example.aryobee.UsersAccountManagment;
public class userModel{
    String name;
    String profileImageUrl;
    String phone;
    String car;

    public userModel(){}

    public userModel(String name, String profileImageUrl, String phone, String car) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.phone = phone;
        this.car = car;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }
}