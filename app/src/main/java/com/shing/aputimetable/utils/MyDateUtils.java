package com.shing.aputimetable.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shing on 29-Jul-17.
 */

public class MyDateUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat();

    /**
     * Return a formatted Date that follow the given Pattern
     *
     * @param date    Date that need to format
     * @param pattern Pattern that need to follow
     * @return {@link String} formatted date string with given pattern
     */
    public static String formatDate(Date date, String pattern) {
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    /**
     * Return the date of Monday for current week
     * except Saturday & Sunday will return the date of Monday for next week
     *
     * @return {@link Date} Date of Monday
     */
    public static Date getMondayDate() {
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);
        if (today != Calendar.SATURDAY) {
            now.add(Calendar.DAY_OF_MONTH, -(today - Calendar.MONDAY)); // get current week monday for everyday except saturday(get next week monday)
        } else {
            now.add(Calendar.DAY_OF_MONTH, 2);//get next week monday for Saturday
        }
        return now.getTime();
    }

    /**
     * Return the day based on given integer
     * EG: 1 = "Mon", 2 = "Tue" etc, only until 5  = Friday
     *
     * @param day integer index for day
     * @return {@link String} Short Form of Day
     */
    public static String getDayFromNum(int day) {
        switch (day) {
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
        }
        return null;
    }

    /**
     * Return today as integer
     * EG: 0 = "Mon", 1 = "Tue" etc, only until 4  = Friday, the rest will be 0 as default
     *
     * @return {@link String} Short Form of Day
     */
    public static int getTodayIndex() {
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
        }
        return 0;

    }
}
