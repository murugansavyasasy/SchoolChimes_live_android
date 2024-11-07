package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class SlotModel {
    private String slot_id;
    private String from_time;
    private String to_time;
    private int is_cancelled;
    private int is_booked;
    private String booked_by;
    private String my_class;
    private String my_section;
    private String profile_url;
    private String status;
    private String event_name;
    private String event_mode;
    //    private String event_mode;
    private int meeting_duration;
    private String break_duration;
    private String date;
    private String subject_name;


    // Getters and setters


    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSlot_id() { return slot_id; }
    public void setSlot_id(String slot_id) { this.slot_id = slot_id; }
    public String getFrom_time() { return from_time; }
    public void setFrom_time(String from_time) { this.from_time = from_time; }
    public String getTo_time() { return to_time; }
    public void setTo_time(String to_time) { this.to_time = to_time; }
    public int getIs_cancelled() { return is_cancelled; }
    public void setIs_cancelled(int is_cancelled) { this.is_cancelled = is_cancelled; }
    public int getIs_booked() { return is_booked; }
    public void setIs_booked(int is_booked) { this.is_booked = is_booked; }
    public String getBooked_by() { return booked_by; }
    public void setBooked_by(String booked_by) { this.booked_by = booked_by; }
    public String getMy_class() { return my_class; }
    public void setMy_class(String my_class) { this.my_class = my_class; }
    public String getMy_section() { return my_section; }
    public void setMy_section(String my_section) { this.my_section = my_section; }
    public String getProfile_url() { return profile_url; }
    public void setProfile_url(String profile_url) { this.profile_url = profile_url; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEvent_name() { return event_name; }
    public void setEvent_name(String event_name) { this.event_name = event_name; }

    public String getEvent_mode() { return event_mode; }
    public void setEvent_mode(String event_mode) { this.event_mode = event_mode; }

//    public String getEvent_mode() { return event_mode; }
//    public void setEvent_mode(String event_mode) { this.event_mode = event_mode; }

    public int getMeeting_duration() { return meeting_duration; }
    public void setMeeting_duration(int meeting_duration) { this.meeting_duration = meeting_duration; }
    public String getBreak_duration() { return break_duration; }
    public void setBreak_duration(String break_duration) { this.break_duration = break_duration; }
}
