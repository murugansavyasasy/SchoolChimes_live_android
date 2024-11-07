package com.vs.schoolmessenger.model;

import java.util.List;

public class DatePicking {
    private String date;
    private List<TimeSlot> slots;

    public DatePicking(String date, List<TimeSlot> slots) {
        this.date = date;
        this.slots = slots;
    }

    public String getDate() {
        return date;
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Slots: " + slots;
    }
}
