package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class SlotsData {


    public int slot_id;
    public String from_time;
    public String to_time;
    public int is_booked;
    public int staff_id;
    public String staff_name;
    public String subject_name;
    public String event_name;
    public String event_mode;
    public String event_link;
    public int my_booking;

    public SlotsData(int slot_id, String from_time, String to_time, int is_booked, int staff_id,
                String staff_name, String subject_name, String event_name, String event_mode,
                String event_link, int my_booking) {
        this.slot_id = slot_id;
        this.from_time = from_time;
        this.to_time = to_time;
        this.is_booked = is_booked;
        this.staff_id = staff_id;
        this.staff_name = staff_name;
        this.subject_name = subject_name;
        this.event_name = event_name;
        this.event_mode = event_mode;
        this.event_link = event_link;
        this.my_booking = my_booking;
    }
}
