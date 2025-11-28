package com.vs.schoolmessenger.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SlotDetail {
    int isBooking;
    String from_time;
    String to_time;
    int slot_id;
    int isStaffId;
    int isSpecificMeeting;
    int isMyBooking;

    public SlotDetail(int isBooking, String from_time, String to_time, int slot_id, int isStaffId, int isSpecificMeeting, int my_booking) {
        this.isBooking=isBooking;
        this.from_time = from_time;
        this.to_time = to_time;
        this.slot_id = slot_id;
        this.isStaffId=isStaffId;
        this.isSpecificMeeting=isSpecificMeeting;
        this.isMyBooking=my_booking;
    }


    public int getIsBooking() {
        return isBooking;
    }

    public void setIsBooking(int isBooking) {
        this.isBooking = isBooking;
    }

    public int getIsSpecificMeeting() {
        return isSpecificMeeting;
    }

    public void setIsSpecificMeeting(int isSpecificMeeting) {
        this.isSpecificMeeting = isSpecificMeeting;
    }

    public int getIsStaffId() {
        return isStaffId;
    }

    public void setIsStaffId(int isStaffId) {
        this.isStaffId = isStaffId;
    }

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

    public int getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(int slot_id) {
        this.slot_id = slot_id;
    }

    public int getIsMyBooking() {
        return isMyBooking;
    }

    public void setIsMyBooking(int isMyBooking) {
        this.isMyBooking = isMyBooking;
    }

    public Date getFromDate(SimpleDateFormat sdf) throws Exception {
        return sdf.parse(from_time);
    }

    public Date getToDate(SimpleDateFormat sdf) throws Exception {
        return sdf.parse(to_time);
    }

    @Override
    public String toString() {
        return "SlotDetail{" +
                "isBooking=" + isBooking +
                "isMyBooking=" + isMyBooking +
                ", from_time='" + from_time + '\'' +
                ", to_time='" + to_time + '\'' +
                ", slot_id=" + slot_id +
                ", isStaffId=" + isStaffId +
                ", isSpecificMeeting=" + isSpecificMeeting +
                '}';
    }
}