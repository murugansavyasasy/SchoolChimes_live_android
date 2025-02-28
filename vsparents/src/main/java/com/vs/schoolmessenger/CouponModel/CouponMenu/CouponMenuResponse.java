package com.vs.schoolmessenger.CouponModel.CouponMenu;

import com.google.gson.annotations.SerializedName;

public class CouponMenuResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CouponMenuData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CouponMenuData getData() {
        return data;
    }

}

