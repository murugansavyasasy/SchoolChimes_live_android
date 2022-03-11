package com.vs.schoolmessenger.model;

public class DayClass {
    String day;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DayClass(String day, String id) {
        this.day = day;
        this.id = id;

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}