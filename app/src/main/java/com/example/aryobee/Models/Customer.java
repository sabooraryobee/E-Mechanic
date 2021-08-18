package com.example.aryobee.Models;

public class Customer {

    String profileImageUrl;
    String name;
    String phone;

    public Customer(){}

    public Customer(String profileImageUrl, String name, String phone) {
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "profileImageUrl='" + profileImageUrl + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
