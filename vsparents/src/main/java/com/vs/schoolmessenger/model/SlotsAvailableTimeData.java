package com.vs.schoolmessenger.model;

import java.util.List;

public class SlotsAvailableTimeData {

    private String institute_id;
    private String staff_id;
    private String break_time;
    private String date;
    private String duration;
    private String event_name;
    private String meeting_mode;
    private String from_time;
    private String to_time;
    private List<slotsTime> data;
    private List<std_sec_details> StandardData;

    public String getInstitute_id() {
        return institute_id;
    }

    public void setInstitute_id(String institute_id) {
        this.institute_id = institute_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getBreak_time() {
        return break_time;
    }

    public void setBreak_time(String break_time) {
        this.break_time = break_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getMeeting_mode() {
        return meeting_mode;
    }

    public void setMeeting_mode(String meeting_mode) {
        this.meeting_mode = meeting_mode;
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

    public List<slotsTime> getData() {
        return data;
    }

    public void setData(List<slotsTime> data) {
        this.data = data;
    }

    public List<std_sec_details> getStandardData() {
        return StandardData;
    }

    public void setStandardData(List<std_sec_details> standardData) {
        StandardData = standardData;
    }


    @Override
    public String toString() {
        return "SlotsAvailableTimeData{" +
                "institute_id='" + institute_id + '\'' +
                ", staff_id='" + staff_id + '\'' +
                ", break_time='" + break_time + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", event_name='" + event_name + '\'' +
                ", meeting_mode='" + meeting_mode + '\'' +
                ", from_time='" + from_time + '\'' +
                ", to_time='" + to_time + '\'' +
                ", slots=" + date +
                ", std_sec_details=" + StandardData +
                '}';
    }


    public SlotsAvailableTimeData(String institute_id, String staff_id, String break_time, String date, String duration, String event_name, String meeting_mode, String from_time, String to_time, List<slotsTime> data, List<std_sec_details> standardData) {
        this.institute_id = institute_id;
        this.staff_id = staff_id;
        this.break_time = break_time;
        this.date = date;
        this.duration = duration;
        this.event_name = event_name;
        this.meeting_mode = meeting_mode;
        this.from_time = from_time;
        this.to_time = to_time;
        this.data = data;
        StandardData = standardData;
    }
}
