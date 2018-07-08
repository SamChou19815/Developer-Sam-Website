package com.developersam.scheduler

import com.developersam.auth.GoogleUser

/**
 * [SchedulerData] is the collection of all data in the scheduler app.
 *
 * @property items a list of all items.
 * @property events a list of all events.
 */
class SchedulerData private constructor(
        val items: List<SchedulerItem>, val events: List<SchedulerEvent>
) {

    /**
     * Construct the [SchedulerData] associated with the given [user].
     */
    constructor(user: GoogleUser) : this(items = SchedulerItem[user], events = SchedulerEvent[user])

}
