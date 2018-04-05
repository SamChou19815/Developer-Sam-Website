@file:JvmName(name = "SchedulerUtil")

package com.developersam.scheduler

import java.util.Calendar
import java.util.Date

/**
 * A helper property to obtain a [yesterday] [Date] object.
 */
internal val yesterday: Date
    get() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }

/**
 * An extension function to add a few more [hours] to the date.
 * This method will not modify the old date but will instead create a new one.
 */
internal fun Date.addHours(hours: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
}
