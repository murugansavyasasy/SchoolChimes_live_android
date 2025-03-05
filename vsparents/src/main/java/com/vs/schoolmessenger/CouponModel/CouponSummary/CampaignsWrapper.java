package com.vs.schoolmessenger.CouponModel.CouponSummary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CampaignsWrapper {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<Summary> data;

    public int getCurrentPage() {
        return currentPage;
    }

    public List<Summary> getData() {
        return data;
    }
}