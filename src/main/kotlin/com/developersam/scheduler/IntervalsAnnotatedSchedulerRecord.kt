package com.developersam.scheduler

/**
 * [IntervalsAnnotatedSchedulerRecord] represents a scheduler record annotated by a list of sorted,
 * not-completed, non-conflicting intervals that is recommended by the system.
 */
data class IntervalsAnnotatedSchedulerRecord(
        val record: SchedulerRecord, val intervals: List<Interval>
)
