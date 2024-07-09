package com.vs.schoolmessenger.model;

public class DailyCollectionFeeReport {

    private long status;
    private String message;
    private DailyFeeData[] data;

    public long getStatus() {
        return status;
    }

    public void setStatus(long value) {
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public DailyFeeData[] getData() {
        return data;
    }

    public void setData(DailyFeeData[] value) {
        this.data = value;
    }
}


