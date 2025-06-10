package com.vs.schoolmessenger.CouponModel.TicketActivateCoupon;

import com.google.gson.annotations.SerializedName;

public class ActivateCouponResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ActivateCouponData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ActivateCouponData getData() {
        return data;
    }
}
