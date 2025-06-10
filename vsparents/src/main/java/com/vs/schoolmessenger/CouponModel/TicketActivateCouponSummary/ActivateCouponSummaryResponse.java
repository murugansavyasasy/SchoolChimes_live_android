package com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary;

import com.google.gson.annotations.SerializedName;

public class ActivateCouponSummaryResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ActivateCouponSummaryData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ActivateCouponSummaryData getData() {
        return data;
    }
}


