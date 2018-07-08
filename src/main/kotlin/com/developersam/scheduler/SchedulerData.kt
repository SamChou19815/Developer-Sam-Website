package com.developersam.scheduler

import com.developersam.auth.GoogleUser

/**
 * [SchedulerData] is the collection of all data in the scheduler app.
 *
 * @property projects a list of all projects.
 * @property events a list of all events.
 */
class SchedulerData private constructor(
        val projects: List<SchedulerProject>, val events: List<SchedulerEvent>
) {

    /**
     * Construct the [SchedulerData] associated with the given [user].
     */
    constructor(user: GoogleUser) : this(
            projects = SchedulerProject[user], events = SchedulerEvent[user]
    )

    companion object {
        /**
         * [empty] is the empty [SchedulerData].
         */
        @JvmField
        val empty: SchedulerData = SchedulerData(projects = emptyList(), events = emptyList())
    }

}
