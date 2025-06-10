package com.vs.schoolmessenger.CouponModel.LogactiveapiResponse;


import com.google.gson.annotations.SerializedName;

public class LogActiveApiResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;


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
}
