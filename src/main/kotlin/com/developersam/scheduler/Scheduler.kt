package com.developersam.scheduler

import com.developersam.database.DatastoreClient
import com.developersam.database.and
import com.developersam.util.Consumer
import com.developersam.util.consumeBy
import com.developersam.util.yesterday
import com.developersam.web.auth.FirebaseUser
import com.google.cloud.Timestamp
import com.google.cloud.datastore.StructuredQuery.PropertyFilter

/**
 * [Scheduler] is the entry point of many operations of the scheduler app.
 */
object Scheduler {

    /**
     * [getAllSchedulerItems] gives a list of [SchedulerItem] for a given
     * [user], which is consumed by the given [consumer].
     *
     * Requires:
     * - The given [user] must exist.
     */
    fun getAllSchedulerItems(user: FirebaseUser,
                             consumer: Consumer<List<SchedulerItem>>) {
        val filterUser = PropertyFilter.eq("userEmail", user.email)
        val filterDeadline = PropertyFilter.ge("deadline",
                Timestamp.of(yesterday))
        val filter = filterUser and filterDeadline
        DatastoreClient.query(kind = "SchedulerItem", filter = filter) { s ->
            s.map(::SchedulerItem)
                    .filter { it.totalHoursLeft >= 0 }
                    .sorted()
                    .toList()
                    .consumeBy(consumer = consumer)
        }
    }

    /**
     * [delete] removes a scheduler item from database with a given [key] if
     * the item really belongs to the given [user].
     */
    fun delete(user: FirebaseUser, key: String) {
        DatastoreClient.deleteEntity(keyString = key) {
            SchedulerItem.fromKey(keyString = it)?.belongsTo(user) == true
        }
    }

    /**
     * [markAs] marks the completion status for a scheduler item specified
     * by the given [key] and the desired new completion status [completed] if
     * the item really belongs to the given [user].
     */
    fun markAs(user: FirebaseUser, key: String, completed: Boolean) {
        SchedulerItem.fromKey(keyString = key)
                ?.takeIf { it.belongsTo(user) }
                ?.markAs(completed = completed)
    }

}