package com.vs.schoolmessenger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    // Method to convert date from "dd/MM/yyyy" to "MMM d, yyyy"
    public static String formatDate(String originalDateStr) {
        // Define input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);

        try {
            // Parse the original date string to a Date object
            Date date = inputFormat.parse(originalDateStr);

            // Format the Date object to the desired output string
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            // Return a meaningful error message or null in case of an error
            return null;
        }
    }
}
