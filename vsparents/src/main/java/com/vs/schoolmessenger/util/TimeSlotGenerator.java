package com.vs.schoolmessenger.util;

import com.vs.schoolmessenger.model.TimeSlot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import android.util.Log;

public class TimeSlotGenerator {

    private static final String TIME_FORMAT = "hh:mm a"; // Define the time format

    public static List<TimeSlot> generateTimeSlots(String fromTime, String toTime, int slotDuration, int breakDuration, int slotsCountBeforeBreak) {
        List<TimeSlot> slots = new ArrayList<>();

        // Parse the 'fromTime' and 'toTime' strings to Date objects
        Date from = parseTime(fromTime);
        Date to = parseTime(toTime);

        if (from == null || to == null) {
            Log.e("TimeSlotGenerator", "Invalid time format for fromTime or toTime");
            return slots; // Return empty if parsing fails
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);

        int slotCounter = 0;

        while (calendar.getTime().before(to)) {
            Date slotStart = calendar.getTime();
            calendar.add(Calendar.MINUTE, slotDuration);
            Date slotEnd = calendar.getTime();

            if (slotEnd.after(to)) {
                break; // Avoid slots that exceed the toTime
            }

            // Log the slot being added
            Log.d("TimeSlotGenerator", "Adding slot: From: " + formatTime(slotStart) + " To: " + formatTime(slotEnd));
            slots.add(new TimeSlot(formatTime(slotStart), formatTime(slotEnd)));

            slotCounter++;

            if (slotCounter == slotsCountBeforeBreak) {
                slotCounter = 0;
                calendar.add(Calendar.MINUTE, breakDuration);
                Log.d("TimeSlotGenerator", "Taking break for " + breakDuration + " minutes");
            }
        }

        return slots;
    }

    // Parse a time string (e.g., "09:00 AM") into a Date object
    private static Date parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            Log.e("TimeSlotGenerator", "Error parsing time: " + time, e);
            return null;
        }
    }

    // Format a Date object into a time string (e.g., "09:00 AM")
    private static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }
}
