package com.vs.schoolmessenger.CouponModel.CouponSummary;


import com.google.gson.annotations.SerializedName;

public class CouponSummaryResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CouponSummaryData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CouponSummaryData getData() {
        return data;
    }
}


