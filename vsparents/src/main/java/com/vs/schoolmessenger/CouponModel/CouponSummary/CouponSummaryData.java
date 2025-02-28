package com.vs.schoolmessenger.CouponModel.CouponSummary;

import com.google.gson.annotations.SerializedName;


public class CouponSummaryData {
    @SerializedName("campaigns")
    private CampaignsWrapper campaigns;

    public CampaignsWrapper getCampaigns() {
        return campaigns;
    }
}