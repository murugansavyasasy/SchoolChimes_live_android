package com.vs.schoolmessenger.model;

public class PaidDetailsList {
    private String InvoiceId, TotalPaid,PaymentType,CreatedOn,LateFee,IsRejected;

    public PaidDetailsList(String invoiceId, String totalPaid,String paymentType,String createdOn,String lateFee,String isRejected  ) {
        this.InvoiceId = invoiceId;
        this.TotalPaid = totalPaid;
        this.PaymentType = paymentType;
        this.CreatedOn = createdOn;
        this.LateFee = lateFee;
        this.IsRejected = isRejected;



    }

    public PaidDetailsList() {}

    public String getInvoiceId() {
        return InvoiceId;
    }

    public void setInvoiceId(String Id) {
        this.InvoiceId = Id;
    }

    public String getTotalPaid() {
        return TotalPaid;
    }

    public void setTotalPaid(String total) {
        this.TotalPaid = total;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String type) {
        this.PaymentType = type;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String create) {
        this.CreatedOn = create;
    }
    public String getLateFee() {
        return LateFee;
    }

    public void setLateFee(String late) {
        this.LateFee = late;
    }

    public String getIsRejected() {
        return IsRejected;
    }

    public void setIsRejected(String reject) {
        this.IsRejected = reject;
    }
}


