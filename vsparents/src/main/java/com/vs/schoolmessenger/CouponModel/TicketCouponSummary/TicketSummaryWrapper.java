package com.vs.schoolmessenger.CouponModel.TicketCouponSummary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TicketSummaryWrapper  {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<TicketSummary> data;

    public int getCurrentPage() {
        return currentPage;
    }

    public List<TicketSummary> getData() {
        return data;
    }
}
