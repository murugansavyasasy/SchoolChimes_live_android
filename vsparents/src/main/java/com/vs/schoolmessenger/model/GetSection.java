package com.vs.schoolmessenger.model;

import java.util.List;

public class GetSection {

    private int Status;
    private String Message;
    private List<GetSectionData> data;

    // Getters and Setters
    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public List<GetSectionData> getData() {
        return data;
    }

    public void setData(List<GetSectionData> data) {
        this.data = data;
    }

}
