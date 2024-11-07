package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class slotsTime {
    private String from_time;
    private String to_time;
    private String type;
    private String slot_Availablity;

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSlot_Availablity() {
        return slot_Availablity;
    }

    public void setSlot_Availablity(String slot_Availablity) {
        this.slot_Availablity = slot_Availablity;
    }
    @Override
    public String toString() {
        return "slotsTime{" +
                "from_time='" + from_time + '\'' +
                ", to_time='" + to_time + '\'' +
                ", type=" + type +
                ", slot_Availablity='" + slot_Availablity + '\'' +
                '}';
    }


    public slotsTime(String from_time, String to_time, String type, String slot_Availablity) {
        this.from_time = from_time;
        this.to_time = to_time;
        this.type = type;
        this.slot_Availablity = slot_Availablity;
    }
}
