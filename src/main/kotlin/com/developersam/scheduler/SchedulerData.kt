package com.developersam.scheduler

import com.developersam.auth.FirebaseUser

/**
 * [SchedulerData] is the collection of all data in the scheduler app.
 *
 * @property items a list of all items.
 * @property events a list of all events.
 */
data class SchedulerData(
        val items: List<SchedulerItem> = emptyList(),
        val events: List<SchedulerEvent> = emptyList()
) {

    companion object {

        /**
         * [get] returns the [SchedulerData] associated with the given [user].
         */
        @JvmStatic
        operator fun get(user: FirebaseUser): SchedulerData =
                SchedulerData(items = SchedulerItem[user], events = SchedulerEvent[user])

    }

}
