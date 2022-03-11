package com.vs.schoolmessenger.model;

public class TimeTableClass {
    String name;
    String fromTime;
    String toTime;
    String duration;
    String hourType;
    String subjectName;
    String staffName;

    public TimeTableClass(String name, String fromTime, String toTime, String duration, String hourType,String subjectName,String staffName){
        this.name = name;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.duration = duration;
        this.hourType = hourType;
        this.subjectName = subjectName;
        this.staffName = staffName;


    }
    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHourType() {
        return hourType;
    }

    public void setHourType(String hourType) {
        this.hourType = hourType;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





}