package com.vs.schoolmessenger.model;

public class HolidayModel {
    private String Date,Reason;

    public HolidayModel(String date, String reason) {
        this.Date = date;
        this.Reason = reason;

    }

    public HolidayModel() {}

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }


}

