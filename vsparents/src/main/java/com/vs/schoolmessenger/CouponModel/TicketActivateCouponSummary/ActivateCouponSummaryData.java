package com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary;

import com.google.gson.annotations.SerializedName;

public class ActivateCouponSummaryData {
    @SerializedName("campaign_details")
    private ActivateCouponSummary campaign_details;

    public ActivateCouponSummary getCampaign_details() {
        return campaign_details;
    }
}
