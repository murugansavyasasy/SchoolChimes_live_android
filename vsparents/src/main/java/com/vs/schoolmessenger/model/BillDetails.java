package com.vs.schoolmessenger.model;

public class BillDetails  {
    private String InvoiceId, FeeName,FeeTerm,PaidAmount,Discount,FeeGroupId,Disc,LateFee;
    private  int SerialNo;

    public BillDetails(String invoiceId, String feeName,String feeTerm,String paidAmount,String discount,String feeGroupId,String disc,String lateFee,int serialNo ) {
        this.InvoiceId = invoiceId;
        this.FeeName = feeName;
        this.FeeTerm = feeTerm;
        this.PaidAmount = paidAmount;
        this.Discount = discount;
        this.FeeGroupId = feeGroupId;
        this.Disc = disc;
        this.LateFee = lateFee;
        this.SerialNo = serialNo;

    }

    public BillDetails() {}

    public String getInvoiceId() {
        return InvoiceId;
    }

    public void setInvoiceId(String Id) {
        this.InvoiceId = Id;
    }

    public String getFeeName() {
        return FeeName;
    }

    public void setFeeName(String feeName) {
        this.FeeName = feeName;
    }

    public String getFeeTerm() {
        return FeeTerm;
    }

    public void setFeeTerm(String term) {
        this.FeeTerm = term;
    }

    public String getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(String amount) {
        this.PaidAmount = amount;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        this.Discount = discount;
    }
    public String getFeeGroupId() {
        return FeeGroupId;
    }

    public void setFeeGroupId(String group) {
        this.FeeGroupId = group;
    }

    public String getDisc() {
        return Disc;
    }

    public void setDisc(String disc) {
        this.Disc = disc;
    }

    public String getLateFee() {
        return LateFee;
    }

    public void setLateFee(String late) {
        this.LateFee = late;
    }


    public int getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(int sn) {
        this.SerialNo = sn;
    }

}


