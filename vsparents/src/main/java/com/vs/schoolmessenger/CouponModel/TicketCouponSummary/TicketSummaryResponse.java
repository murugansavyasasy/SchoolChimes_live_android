package com.vs.schoolmessenger.CouponModel.TicketCouponSummary;

import com.google.gson.annotations.SerializedName;

public class TicketSummaryResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private TicketSummaryData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public TicketSummaryData getData() {
        return data;
    }
}


