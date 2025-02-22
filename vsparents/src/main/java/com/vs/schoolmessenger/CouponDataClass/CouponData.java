package com.vs.schoolmessenger.CouponDataClass;

public class CouponData {
    private int totalCount;
    private CampaignPage campaigns;

    // Constructor
    public CouponData(int totalCount, CampaignPage campaigns) {
        this.totalCount = totalCount;
        this.campaigns = campaigns;
    }

    // Getters
    public int getTotalCount() {
        return totalCount;
    }

    public CampaignPage getCampaigns() {
        return campaigns;
    }

    // Setters
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setCampaigns(CampaignPage campaigns) {
        this.campaigns = campaigns;
    }


}
