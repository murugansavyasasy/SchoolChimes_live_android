package com.vs.schoolmessenger.util;

public class DateUtils {

    public static String[] splitDate(String date) {
        // Remove the comma and split the date into components
        return date.replace(",", "").split(" ");
    }
}
