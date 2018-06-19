package com.developersam.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * The commonly used date format throughout the application.
 */
private const val commonDateFormat = "yyyy-MM-dd"

/**
 * A helper method to initialize the data with consistent time zone.
 */
private fun initializedDateFormatter(): SimpleDateFormat {
    val formatter = SimpleDateFormat(commonDateFormat)
    formatter.timeZone = TimeZone.getTimeZone("America/New_York")
    return formatter
}

/**
 * A consistently used date formatter.
 */
private val dateFormatter = initializedDateFormatter()

/**
 * Convert a [Date] object to [String] in format yyyy-MM-dd in EST.
 */
fun dateToString(date: Date): String = dateFormatter.format(date)

/**
 * Convert a date [String] of format yyyy-MM-dd to a [Date] object in EST.
 */
fun stringToDate(date: String): Date? {
    return try {
        dateFormatter.parse(date)
    } catch (e: ParseException) {
        null
    }
}
