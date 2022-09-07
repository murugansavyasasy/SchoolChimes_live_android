package com.vs.schoolmessenger.model;

/**
 * Created by voicesnap on 11/28/2017.
 */

public class DatesModel
{
    private String date, day, count;
    private  Boolean is_Archive;

    public DatesModel(String date, String day, String count,Boolean is_Archive) {
        this.date = date;
        this.day = day;
        this.count = count;
        this.is_Archive = is_Archive;
    }

    public DatesModel() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Boolean getIs_Archive() {
        return is_Archive;
    }

    public void setIs_Archive(Boolean archive) {
        this.is_Archive = archive;
    }
}
