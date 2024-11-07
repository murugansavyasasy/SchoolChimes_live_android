package com.vs.schoolmessenger.model;

public class DateEventModel {
    private String eventDate;
    private int count;

    public DateEventModel(String eventDate, int count) {
        this.eventDate = eventDate;
        this.count = count;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
