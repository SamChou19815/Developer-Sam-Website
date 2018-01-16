package com.developersam.model.scheduler

import com.developersam.util.datastore.DataStoreObject
import com.developersam.util.datastore.dataStore
import com.developersam.util.yesterday
import com.google.appengine.api.datastore.Query.CompositeFilterOperator
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.FilterPredicate
import com.google.appengine.api.datastore.Query.SortDirection
import com.google.appengine.api.users.UserServiceFactory
import java.util.ArrayList
import java.util.stream.StreamSupport

/**
 * Represent the scheduler app.
 */
object Scheduler : DataStoreObject(kind = "SchedulerItem") {

    /**
     * Obtain a list of [allSchedulerItems] for a signed-in user.
     */
    val allSchedulerItems: Array<SchedulerItem>
        get() {
            val userEmail = UserServiceFactory.getUserService()
                    .currentUser.email
            val filterUser = FilterPredicate("userEmail",
                    FilterOperator.EQUAL, userEmail)
            val filterDeadline = FilterPredicate("deadline",
                    FilterOperator.GREATER_THAN_OR_EQUAL, yesterday)
            val trueAndFalse = ArrayList<Boolean>(2)
            trueAndFalse.add(true)
            trueAndFalse.add(false)
            val filterCompleted = FilterPredicate(
                    "completed", FilterOperator.IN, trueAndFalse)
            val filter = CompositeFilterOperator.and(
                    filterCompleted, filterUser, filterDeadline)
            val q = query.setFilter(filter)
                    .addSort("completed", SortDirection.ASCENDING)
                    .addSort("deadline", SortDirection.ASCENDING)
            // :< Just to overcome Datastore's indexing and sorting limitation.
            val pq = dataStore.prepare(q)
            return StreamSupport.stream(
                    pq.asIterable().spliterator(), false)
                    .map({ SchedulerItem(it) })
                    .toArray({ size -> arrayOfNulls<SchedulerItem>(size) })
        }

    /**
     * Delete a scheduler item with a given [key].
     */
    fun delete(key: String) {
        val item = SchedulerItem.from(key)
        item?.deleteFromDatabase()
    }

    /**
     * Mark the completion status for a scheduler item specified by the
     * given [key] and the desired new [completionStatus].
     */
    fun markAs(key: String, completionStatus: Boolean) {
        val item = SchedulerItem.from(key)
        item?.markAs(completionStatus)
    }

}