package com.vs.schoolmessenger.model;

public class DailyFeeReportData {
    private String feeName;
    private String   amount;

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String value) {
        this.feeName = value;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String  value) {
        this.amount = value;
    }

    public DailyFeeReportData(String feeName, String  amount){
        this.feeName = feeName;
        this.amount = amount;
    }
}
