package com.developersam.scheduler

/**
 * [AnnotatedSchedulerRecord] represents a scheduler record annotated by a list of sorted,
 * not-completed, non-conflicting intervals that is recommended by the system.
 */
data class AnnotatedSchedulerRecord(
        val record: SchedulerRecord, val intervals: List<Interval>
)
