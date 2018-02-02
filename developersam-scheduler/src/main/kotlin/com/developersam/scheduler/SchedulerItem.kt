package com.developersam.scheduler

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.Deletable
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.datastore.getEntityByKey
import com.developersam.webcore.date.addHours
import com.developersam.webcore.exception.AccessDeniedException
import com.developersam.webcore.service.GoogleUserService
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.KeyFactory
import com.google.common.base.MoreObjects
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * An individual item in the scheduler.
 * It consists of description, deadline, and a completion status.
 *
 * Construct itself from an [entity] fetched from database.
 */
class SchedulerItem internal constructor(
        @field:Transient private val entity: Entity
) : DataStoreObject(kind = "SchedulerItem"), Deletable {

    /**
     * The key string of the entity.
     */
    private val keyString: String = KeyFactory.keyToString(entity.key)
    /**
     * Description of the item.
     */
    private val description: String =
            entity.getProperty("description") as String
    /**
     * Deadline of the item.
     */
    private val deadline: Date = entity.getProperty("deadline") as Date
    /**
     * Deadline of the item with precision to hours,
     * which is completely optional.
     */
    private val deadlineHour: Int? =
            (entity.getProperty("deadlineHour") as Long?)?.toInt()
    /**
     * Total hours left, used for filtering outdated [SchedulerItem].
     */
    @field:Transient
    internal val totalHoursLeft: Int

    /*
     * Help calculate total hours left.
     */
    init {
        val actualDeadlineHour: Int = deadlineHour ?: 24
        val actualDeadlineDate = deadline.addHours(hours = actualDeadlineHour)
        val diff = actualDeadlineDate.time - Date().time
        totalHoursLeft = TimeUnit.MILLISECONDS.toHours(diff).toInt()
    }

    /**
     * Days left.
     */
    private val daysLeft: Int = totalHoursLeft / 24
    /**
     * Hours left.
     */
    private val hoursLeft: Int = totalHoursLeft % 24
    /**
     * Optional field of estimated hours.
     */
    private val estimatedHours: Int? =
            (entity.getProperty("estimatedHours") as Long?)?.toInt()
    /**
     * Optional field of estimated progress.
     */
    private val estimatedProgress: Int? =
            (entity.getProperty("estimatedProgress") as Long?)?.toInt()
    /**
     * Whether the item has been completed.
     */
    private val isCompleted: Boolean =
            entity.getProperty("completed") as Boolean
    /**
     * The details of an item, which is completely optional.
     */
    private val detail: String? = entity.getProperty("detail") as String?

    init {
        if (!((estimatedHours != null) xor (estimatedProgress == null))) {
            throw Error("Inconsistency of estimated value existence!")
        }
    }

    /**
     * A helper property to check whether the user is the owner of the item.
     */
    private val isOwner: Boolean
        get() {
            val email: String = GoogleUserService.currentUser?.email
                    ?: throw AccessDeniedException()
            val itemEmail = entity.getProperty("userEmail") as String
            return email == itemEmail
        }

    override fun deleteFromDatabase(): Boolean {
        return if (isOwner) {
            dataStore.delete(entity.key)
            true
        } else {
            false
        }
    }

    /**
     * Mark the item as completed or not.
     *
     * @param completed whether the item should be marked as completed or not.
     */
    internal fun markAs(completed: Boolean) {
        if (isOwner) {
            entity.setProperty("completed", completed)
            dataStore.put(entity)
        }
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("entity", entity)
                .add("keyString", keyString)
                .add("description", description)
                .add("deadline", deadline)
                .add("daysLeft", daysLeft)
                .add("deadlineHour", deadlineHour)
                .add("hoursLeft", hoursLeft)
                .add("estimatedHours", estimatedHours)
                .add("estimatedProgress", estimatedProgress)
                .add("completed", isCompleted)
                .add("detail", detail)
                .toString()
    }

    companion object Factory {

        /**
         * Construct a scheduler item from a unique [keyString], which may fail
         * due to invalid keep and return a `null`.
         */
        fun from(keyString: String): SchedulerItem? {
            val entity = dataStore.getEntityByKey(keyString) ?: return null
            return SchedulerItem(entity)
        }

    }

}