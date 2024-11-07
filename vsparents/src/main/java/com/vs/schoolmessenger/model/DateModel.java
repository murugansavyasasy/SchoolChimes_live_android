package com.vs.schoolmessenger.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import java.util.List;

public class DateModel {
    private String date;
    private List<DetailsModel> details;

    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<DetailsModel> getDetails() { return details; }
    public void setDetails(List<DetailsModel> details) { this.details = details; }
}
