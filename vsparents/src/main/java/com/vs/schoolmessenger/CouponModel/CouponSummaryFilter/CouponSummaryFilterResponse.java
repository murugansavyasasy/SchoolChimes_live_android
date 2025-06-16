package com.vs.schoolmessenger.CouponModel.CouponSummaryFilter;

import com.google.gson.annotations.SerializedName;
import com.vs.schoolmessenger.CouponModel.CouponSummary.CouponSummaryData;

public class CouponSummaryFilterResponse {
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
