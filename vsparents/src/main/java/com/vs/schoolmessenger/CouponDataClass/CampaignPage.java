package com.vs.schoolmessenger.CouponDataClass;

import com.vs.schoolmessenger.CouponModelClass.Campaign;

import java.util.List;

public class CampaignPage {
    private int currentPage;
    private List<Campaign> data;

    // Constructor
    public CampaignPage(int currentPage, List<Campaign> data) {
        this.currentPage = currentPage;
        this.data = data;
    }

    // Getters
    public int getCurrentPage() {
        return currentPage;
    }

    public List<Campaign> getData() {
        return data;
    }

    // Setters
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setData(List<Campaign> data) {
        this.data = data;
    }

}
