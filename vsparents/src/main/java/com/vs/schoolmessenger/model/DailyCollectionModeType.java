package com.vs.schoolmessenger.model;

public class DailyCollectionModeType {

    private long status;
    private String message;
    private DailyCollectionModeTypeData[] data;

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

    public DailyCollectionModeTypeData[] getData() {
        return data;
    }

    public void setData(DailyCollectionModeTypeData[] value) {
        this.data = value;
    }
}
