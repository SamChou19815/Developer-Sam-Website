package com.developersam.web.util;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Operations related to date.
 */
public final class DateUtil {
    
    /**
     * The commonly used date format throughout the application.
     */
    static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * A consistently used date formatter.
     */
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat(DATE_FORMAT);
    /**
     * A calender used to find time.
     */
    private static final Calendar CALENDAR = Calendar.getInstance();
    
    static {
        // Statically initialize the time zone.
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }
    
    /**
     * Convert a date object to string in format yyyy-MM-dd in EST.
     *
     * @param date date object
     * @return a string representation of time in EST (US New York)
     */
    public static String dateToString(Date date) {
        synchronized (CALENDAR) {
            CALENDAR.setTime(date);
            return DATE_FORMATTER.format(CALENDAR.getTime());
        }
    }
    
    /**
     * Convert a date string of format yyyy-MM-dd to a date object in EST.
     *
     * @param date string representation of the day
     * @return a date object, or {@code null} if the string cannot be parsed.
     */
    @Nullable
    public static Date stringToDate(String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * A helper method to obtain yesterday.
     *
     * @return yesterday.
     */
    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
    
}
