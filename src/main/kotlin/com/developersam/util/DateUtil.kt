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
@JvmField
internal val dateFormatter = initializedDateFormatter()

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

/**
 * A helper property to obtain a [yesterday] [Date] object.
 */
val yesterday: Date
    get() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }

/**
 * A helper method to add a few [amount] of time with the specified unit ([fieldName]) to a given
 * [date].
 * This method will not modify the old date but will instead create a new one.
 */
private fun add(date: Date, fieldName: Int, amount: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(fieldName, amount)
    return calendar.time
}

/**
 * An extension function to add a few more [seconds] to the date.
 * This method will not modify the old date but will instead create a new one.
 */
fun Date.addSeconds(seconds: Int): Date =
        add(date = this, fieldName = Calendar.SECOND, amount = seconds)

/**
 * An extension function to add a few more [minutes] to the date.
 * This method will not modify the old date but will instead create a new one.
 */
fun Date.addMinutes(minutes: Int): Date =
        add(date = this, fieldName = Calendar.MINUTE, amount = minutes)

/**
 * An extension function to add a few more [hours] to the date.
 * This method will not modify the old date but will instead create a new one.
 */
fun Date.addHours(hours: Int): Date =
        add(date = this, fieldName = Calendar.HOUR_OF_DAY, amount = hours)

/**
 * An extension function to add a few more [days] to the date.
 * This method will not modify the old date but will instead create a new one.
 */
fun Date.addDays(days: Int): Date =
        add(date = this, fieldName = Calendar.DAY_OF_MONTH, amount = days)
