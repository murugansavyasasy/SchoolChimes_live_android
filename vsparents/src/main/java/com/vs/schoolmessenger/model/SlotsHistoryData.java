package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class SlotsHistoryData {

    @SerializedName("slot_id")
    String slot_id;

    @SerializedName("slot_date")
    String slot_date;

    @SerializedName("slot_time")
    String slot_time;

    @SerializedName("status")
    String status;

    @SerializedName("purpose")
    String purpose;

    @SerializedName("mode")
    String mode;

    @SerializedName("event_link")
    String event_link;


    @SerializedName("staff_id")
    String staff_id;

    @SerializedName("staff_name")
    String staff_name;

    @SerializedName("subject_name")
    String subject_name;


    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_date(String slot_date) {
        this.slot_date = slot_date;
    }

    public String getSlot_date() {
        return slot_date;
    }


    public void setSlot_time(String slot_time) {
        this.slot_time = slot_time;
    }

    public String getSlot_time() {
        return slot_time;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPurpose() {
        return purpose;
    }


    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setEvent_link(String event_link) {
        this.event_link = event_link;
    }

    public String getEvent_link() {
        return event_link;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public SlotsHistoryData(String slot_id, String slot_date, String slot_time, String status, String purpose, String mode, String event_link, String staff_id, String staff_name,String isSubjectName) {
        this.slot_id = slot_id;
        this.slot_date = slot_date;
        this.slot_time = slot_time;
        this.status = status;
        this.purpose = purpose;
        this.mode = mode;
        this.event_link = event_link;
        this.staff_id = staff_id;
        this.staff_name = staff_name;
        this.subject_name=isSubjectName;
    }
}
