package com.vs.schoolmessenger.model;

public class InVoiceDetailsModel {


    String invoiceNo;
    String invoiceAmount;
    String invoiceDate;
    String id;
    public InVoiceDetailsModel(String invoice_ID,String invoice_number,  String invoice_date,String invoice_amount ) {
        this.id = invoice_ID;
        this.invoiceNo = invoice_number;
        this.invoiceDate = invoice_date;
        this.invoiceAmount = invoice_amount;


    }


    public String getInvoice_ID() {
        return id;
    }

    public void setInvoice_ID(String invoice_ID) {
        this.id = invoice_ID;
    }

    public String getInvoice_number() {
        return invoiceNo;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoiceNo = invoice_number;
    }

    public String getInvoice_amount() {
        return invoiceAmount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoiceAmount = invoice_amount;
    }

    public String getInvoice_date() {
        return invoiceDate;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoiceDate = invoice_date;
    }
}
