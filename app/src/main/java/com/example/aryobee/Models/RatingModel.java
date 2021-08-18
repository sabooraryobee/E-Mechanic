package com.example.aryobee.Models;

public class RatingModel {
    String customerID;
    String mechanicID;
    String comment;
    double rate;

   public RatingModel(){}
    public RatingModel(String customerID, String mechanicID, String comment, double rate) {
        this.customerID = customerID;
        this.mechanicID = mechanicID;
        this.comment = comment;
        this.rate = rate;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getMechanicID() {
        return mechanicID;
    }

    public void setMechanicID(String mechanicID) {
        this.mechanicID = mechanicID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
