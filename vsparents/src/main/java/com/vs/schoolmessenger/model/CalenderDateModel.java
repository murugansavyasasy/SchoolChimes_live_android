package com.vs.schoolmessenger.model;

public class CalenderDateModel {
    private String date;
    private String dayOfWeek;
    private String month;
    private String year;
    private String eventDate;
    private String count;

    public CalenderDateModel(String date, String dayOfWeek, String month, String year) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

//    public String getEventDate() {
//        return eventDate;
//    }
//
//    public String getCount() {
//        return count;
//    }
}
