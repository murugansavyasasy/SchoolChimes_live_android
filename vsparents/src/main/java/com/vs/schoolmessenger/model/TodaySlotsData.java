package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class TodaySlotsData {

    @SerializedName("eventDate")
    String eventDate;

    @SerializedName("slotId")
    String slotId;

    @SerializedName("slotFrom")
    String slotFrom;

    @SerializedName("slotTo")
    String slotTo;

    @SerializedName("eventName")
    String eventName;

    @SerializedName("eventMode")
    String eventMode;

    @SerializedName("eventLink")
    String eventLink;


    @SerializedName("studentId")
    String studentId;

    @SerializedName("studentName")
    String studentName;

    @SerializedName("classId")
    String classId;


    @SerializedName("sectionId")
    String sectionId;

    @SerializedName("className")
    String className;


    @SerializedName("sectionName")
    String sectionName;

    @SerializedName("slotStatus")
    String slotStatus;

    @SerializedName("isBooked")
    String isBooked;


    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotId() {
        return slotId;
    }


    public void setSlotTo(String slotFrom) {
        this.slotFrom = slotFrom;
    }

    public String getSlotTo() {
        return slotFrom;
    }


    public void setSlotFrom(String slotFrom) {
        this.slotFrom = slotFrom;
    }

    public String getSlotFrom() {
        return slotFrom;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }


    public void setEventMode(String eventMode) {
        this.eventMode = eventMode;
    }

    public String getEventMode() {
        return eventMode;
    }


    public void setEventLink(String eventLink) {
        this.eventLink = eventLink;
    }

    public String getEventLink() {
        return eventLink;
    }


    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }


    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }


    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }


    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }


    public void setSlotStatus(String slotStatus) {
        this.slotStatus = slotStatus;
    }

    public String getSlotStatus() {
        return slotStatus;
    }


    public void setIsBooked(String isBooked) {
        this.isBooked = isBooked;
    }

    public String getIsBooked() {
        return isBooked;
    }


    public TodaySlotsData(String eventDate, String slotId, String slotFrom, String slotTo, String eventName, String eventMode, String eventLink, String studentId, String studentName, String classId, String sectionId,String className,String sectionName, String slotStatus, String isBooked) {
        this.eventDate = eventDate;
        this.slotId = slotId;
        this.slotFrom = slotFrom;
        this.slotTo = slotTo;
        this.eventName = eventName;
        this.eventMode = eventMode;
        this.eventLink = eventLink;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classId = classId;
        this.sectionId = sectionId;
        this.className=className;
        this.sectionName=sectionName;
        this.slotStatus = slotStatus;
        this.isBooked = isBooked;
    }
}
