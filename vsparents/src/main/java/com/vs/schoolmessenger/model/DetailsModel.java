package com.vs.schoolmessenger.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import java.util.List;

public class DetailsModel {
    private String event_name;
    private String event_mode;
    private int meeting_duration;
    private String break_duration;
    private List<SlotModel> slots;
    private List<StdSecDetailsModel> std_sec_details;

    // Getters and setters
    public String getEvent_name() { return event_name; }
    public void setEvent_name(String event_name) { this.event_name = event_name; }
    public String getEvent_mode() { return event_mode; }
    public void setEvent_mode(String event_mode) { this.event_mode = event_mode; }
    public int getMeeting_duration() { return meeting_duration; }
    public void setMeeting_duration(int meeting_duration) { this.meeting_duration = meeting_duration; }
    public String getBreak_duration() { return break_duration; }
    public void setBreak_duration(String break_duration) { this.break_duration = break_duration; }
    public List<SlotModel> getSlots() { return slots; }
    public void setSlots(List<SlotModel> slots) { this.slots = slots; }
    public List<StdSecDetailsModel> getStd_sec_details() { return std_sec_details; }
    public void setStd_sec_details(List<StdSecDetailsModel> std_sec_details) { this.std_sec_details = std_sec_details; }
}
