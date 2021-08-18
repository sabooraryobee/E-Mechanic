package com.example.aryobee.UsersAccountManagment;

public class Ratings {
    double totalRating;
    int totalCount;

    public Ratings(){}

    public Ratings(double totalRating, int totalCount) {
        this.totalRating = totalRating;
        this.totalCount = totalCount;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
