package com.vs.schoolmessenger.CouponModel.CouponCoin;

public class PointsResponse {
    private int status;
    private String message;
    private PointsData data;

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PointsData getData() {
        return data;
    }

    public void setData(PointsData data) {
        this.data = data;
    }
}
