package com.vs.schoolmessenger.model;

public class DailyCollectionModeTypeData {

    private String paymentMode;
    private double amount;
    private int paymentType;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String value) {
        this.paymentMode = value;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double value) {
        this.amount = value;
    }

    public long getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int value) {
        this.paymentType = value;
    }

    public DailyCollectionModeTypeData(String paymentMode, int amount, int paymentType) {
        this.paymentMode = paymentMode;
        this.amount = amount;
        this.paymentType = paymentType;
    }

}
