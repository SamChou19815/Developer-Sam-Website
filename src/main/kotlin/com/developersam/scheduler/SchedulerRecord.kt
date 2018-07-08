package com.developersam.scheduler

import com.google.cloud.datastore.Key

/**
 * [SchedulerRecord] is designed to be the common super type of [SchedulerProject] and
 * [SchedulerEvent].
 */
interface SchedulerRecord {
    /**
     * [key] of the record.
     */
    val key: Key?
    /**
     * [title] of the record.
     */
    val title: String
}