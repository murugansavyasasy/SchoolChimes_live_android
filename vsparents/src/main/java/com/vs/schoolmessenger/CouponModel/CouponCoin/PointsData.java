package com.vs.schoolmessenger.CouponModel.CouponCoin;

public class PointsData {
    private int pointsEarned;
    private int pointsSpent;
    private int pointsRemaining;

    public int getPointsPerCoupon() {
        return pointsPerCoupon;
    }

    public void setPointsPerCoupon(int pointsPerCoupon) {
        this.pointsPerCoupon = pointsPerCoupon;
    }

    private int pointsPerCoupon;

    // Getters and Setters
    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public int getPointsSpent() {
        return pointsSpent;
    }

    public void setPointsSpent(int pointsSpent) {
        this.pointsSpent = pointsSpent;
    }

    public int getPointsRemaining() {
        return pointsRemaining;
    }

    public void setPointsRemaining(int pointsRemaining) {
        this.pointsRemaining = pointsRemaining;
    }
}
