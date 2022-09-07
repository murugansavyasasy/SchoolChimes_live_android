package com.vs.schoolmessenger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utility {
    static List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Nov", "Dec");

    public static String dateConverter(String date) {
        String[] dateSplit = date.split(" ", 4);
        return String.format("%s %s/%s/%s", dateSplit[1], months.indexOf(dateSplit[0]) + 1, dateSplit[2], dateSplit[3]);
    }

    public static String dateDifference(String d) {
        String day="";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        try {
            Date date = sdf.parse(d);
            long diff = new Date().getTime() - date.getTime();
            int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            if (diffDays == 0) {
                day = "Today";
            } else if (diffDays == 1) {
                day = diffDays + " day";
            } else {
                day = diffDays + " days";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }
}
