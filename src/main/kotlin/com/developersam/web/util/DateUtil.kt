package com.developersam.web.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Operations related to date.
 */
object DateUtil {

    /**
     * The commonly used date format throughout the application.
     */
    @JvmField internal val DATE_FORMAT = "yyyy-MM-dd"
    /**
     * A consistently used date formatter.
     */
    private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT)
    /**
     * A calender used to find time.
     */
    private val CALENDAR = Calendar.getInstance()

    /**
     * A helper method to obtain yesterday.
     *
     * @return yesterday.
     */
    val yesterday: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            return calendar.time
        }

    init {
        // Statically initialize the time zone.
        DATE_FORMATTER.timeZone = TimeZone.getTimeZone("America/New_York")
    }

    /**
     * Convert a date object to string in format yyyy-MM-dd in EST.
     *
     * @param date date object
     * @return a string representation of time in EST (US New York)
     */
    fun dateToString(date: Date): String {
        synchronized(CALENDAR) {
            CALENDAR.time = date
            return DATE_FORMATTER.format(CALENDAR.time)
        }
    }

    /**
     * Convert a date string of format yyyy-MM-dd to a date object in EST.
     *
     * @param date string representation of the day
     * @return a date object, or `null` if the string cannot be parsed.
     */
    fun stringToDate(date: String): Date? {
        return try {
            DATE_FORMATTER.parse(date)
        } catch (e: ParseException) {
            null
        }
    }

}
