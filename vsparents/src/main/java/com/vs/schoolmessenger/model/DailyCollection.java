package com.vs.schoolmessenger.model;
import java.util.List;

public class DailyCollection {
//    @SerializedName("Status")
    private int Status;

//    @SerializedName("message")
    private String message;

//    @SerializedName("data")
    private List<DailyCollectionData> data;


    public void setStatus(int status) {
        this.Status = status;
    }
    public int getStatus() {
        return Status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(List<DailyCollectionData> data) {
        this.data = data;
    }
    public List<DailyCollectionData> getData() {
        return data;
    }
}
