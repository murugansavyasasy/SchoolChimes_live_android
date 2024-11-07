package com.vs.schoolmessenger.model;

import java.util.ArrayList;
import java.util.List;

public class GroupedSlot {
    int is_booked;
    int staff_id;
    String staff_name;
    String subject_name;
    String event_name;
    String event_mode;
    String event_link;
    int my_booking;
    int isSpecificMeeting;
    List<SlotDetail> slots;

    public GroupedSlot(int is_booked, int staff_id, String staff_name, String subject_name, String event_name, String event_mode, String event_link, int my_booking,int isSpecificMeeting) {
        this.is_booked = is_booked;
        this.staff_id = staff_id;
        this.staff_name = staff_name;
        this.subject_name = subject_name;
        this.event_name = event_name;
        this.event_mode = event_mode;
        this.event_link = event_link;
        this.my_booking = my_booking;
        this.slots = new ArrayList<>();
        this.isSpecificMeeting=isSpecificMeeting;
    }

    public int getIsSpecificMeeting() {
        return isSpecificMeeting;
    }

    public void setIsSpecificMeeting(int isSpecificMeeting) {
        this.isSpecificMeeting = isSpecificMeeting;
    }

    public int getIs_booked() {
        return is_booked;
    }

    public void setIs_booked(int is_booked) {
        this.is_booked = is_booked;
    }

    public int getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(int staff_id) {
        this.staff_id = staff_id;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_mode() {
        return event_mode;
    }

    public void setEvent_mode(String event_mode) {
        this.event_mode = event_mode;
    }

    public String getEvent_link() {
        return event_link;
    }

    public void setEvent_link(String event_link) {
        this.event_link = event_link;
    }

    public int getMy_booking() {
        return my_booking;
    }

    public void setMy_booking(int my_booking) {
        this.my_booking = my_booking;
    }

    public List<SlotDetail> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotDetail> slots) {
        this.slots = slots;
    }

    public void addSlotDetail(SlotDetail slotDetail) {
        this.slots.add(slotDetail);
    }

    @Override
    public String toString() {
        // Convert slots to a formatted string without surrounding brackets
        StringBuilder slotsStringBuilder = new StringBuilder();
        for (int i = 0; i < slots.size(); i++) {
            slotsStringBuilder.append(slots.get(i).toString());
            if (i < slots.size() - 1) {
                slotsStringBuilder.append(", ");
            }
        }

        return "{" +
                "\n  \"is_booked\": " + is_booked + "," +
                "\n  \"staff_id\": " + staff_id + "," +
                "\n  \"staff_name\": \"" + staff_name + "\"," +
                "\n  \"subject_name\": \"" + subject_name + "\"," +
                "\n  \"event_name\": \"" + event_name + "\"," +
                "\n  \"event_mode\": \"" + event_mode + "\"," +
                "\n  \"event_link\": \"" + event_link + "\"," +
                "\n  \"my_booking\": " + my_booking + "," +
                "\n  \"slots\": [" + slotsStringBuilder.toString() + "]" +
                "\n}";
    }
}

