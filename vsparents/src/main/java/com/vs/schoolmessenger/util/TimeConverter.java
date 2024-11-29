package com.vs.schoolmessenger.util;

public class TimeConverter {
    public static int convertToSeconds(String time) {
        // Split the time string into minutes and seconds
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]); // Extract minutes
        int seconds = Integer.parseInt(parts[1]); // Extract seconds

        // Convert the time to total seconds
        return (minutes * 60) + seconds;
    }
}
