package com.vs.schoolmessenger.model;

public class MonthlyPending {
    private String FeeName, Monthly, TotalMonthly, StartMonthName, EndMonthName,PendingAmount;

    public MonthlyPending(String feeName, String monthly, String totalMonthly, String startMonthName, String endMonthName,String pendingAmout) {
        this.FeeName = feeName;
        this.Monthly = monthly;
        this.TotalMonthly = totalMonthly;
        this.StartMonthName = startMonthName;
        this.EndMonthName = endMonthName;
        this.PendingAmount = pendingAmout;
    }
    public MonthlyPending() {
    }

    public String getPendingAmount() {
        return PendingAmount;
    }

    public void setPendingAmount(String pending) {
        this.PendingAmount = pending;
    }


    public String getFeeName() {
        return FeeName;
    }

    public void setFeeName(String feename) {
        this.FeeName = feename;
    }

    public String getMonthly() {
        return Monthly;
    }

    public void setMonthly(String monthly) {
        this.Monthly = monthly;
    }

    public String getTotalMonthly() {
        return TotalMonthly;
    }

    public void setTotalMonthly(String total) {
        this.TotalMonthly = total;
    }

    public String getStartMonthName() {
        return StartMonthName;
    }

    public void setStartMonthName(String start) {
        this.StartMonthName = start;
    }

    public String getEndMonthName() {
        return EndMonthName;
    }

    public void setEndMonthName(String end) {
        this.EndMonthName = end;
    }


}



