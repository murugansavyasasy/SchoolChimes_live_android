package com.vs.schoolmessenger.model;

public class TimeSlot {
    private String fromTime;
    private String toTime;

    public TimeSlot(String fromTime, String toTime) {
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }





    @Override
    public String toString() {
        return "From: " + fromTime + " To: " + toTime;
    }
}
