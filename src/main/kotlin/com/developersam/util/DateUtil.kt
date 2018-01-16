@file:JvmName(name = "DateUtil")

package com.developersam.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * The commonly used date format throughout the application.
 */
@JvmField
val commonDateFormat = "yyyy-MM-dd"

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
 * A calendar used to find time.
 */
private val calendar = Calendar.getInstance()

/**
 * Convert a [Date] object to [String] in format yyyy-MM-dd in EST.
 */
fun dateToString(date: Date): String {
    synchronized(calendar) {
        calendar.time = date
        return dateFormatter.format(calendar.time)
    }
}

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

/**
 * A helper method to obtain a [yesterday] [Date] object.
 */
val yesterday: Date
    get() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }