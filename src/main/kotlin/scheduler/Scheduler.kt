package scheduler

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.date.yesterday
import com.developersam.webcore.exception.AccessDeniedException
import com.developersam.webcore.service.GoogleUserService
import com.google.appengine.api.datastore.Query.CompositeFilterOperator
import com.google.appengine.api.datastore.Query.FilterOperator
import com.google.appengine.api.datastore.Query.FilterPredicate
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
            val userEmail: String = GoogleUserService.currentUser?.email
                    ?: throw AccessDeniedException()
            val filterUser = FilterPredicate("userEmail",
                    FilterOperator.EQUAL, userEmail)
            val filterDeadline = FilterPredicate("deadline",
                    FilterOperator.GREATER_THAN_OR_EQUAL, yesterday)
            val filter = CompositeFilterOperator.and(filterUser, filterDeadline)
            val pq = dataStore.prepare(query.setFilter(filter))
            return StreamSupport.stream(
                    pq.asIterable().spliterator(), false)
                    .map { SchedulerItem(it) }
                    .filter { it.totalHoursLeft >= 0 }
                    .sorted { o1, o2 ->
                        val c: Int = o1.isCompleted.compareTo(o2.isCompleted)
                        if (c != 0) {
                            c
                        } else {
                            o1.totalHoursLeft.compareTo(o2.totalHoursLeft)
                        }
                    }
                    .toArray { size -> arrayOfNulls<SchedulerItem>(size) }
        }

    /**
     * Delete a scheduler item with a given [key].
     */
    fun delete(key: String) {
        val item = SchedulerItem.fromKey(keyString = key)
        item?.deleteFromDatabase()
    }

    /**
     * Mark the completion status for a scheduler item specified by the
     * given [key] and the desired new [completionStatus].
     */
    fun markAs(key: String, completionStatus: Boolean) {
        val item = SchedulerItem.fromKey(keyString = key)
        item?.markAs(completionStatus)
    }

}