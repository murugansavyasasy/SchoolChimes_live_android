package com.vs.schoolmessenger.model;

import java.util.List;

public class PtmSubjectdata {

    private int Status;
    private String Message;
    private List<PtmSubject> data;

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

    public List<PtmSubject> getData() {
        return data;
    }

    public void setData(List<PtmSubject> data) {
        this.data = data;
    }
}



