package com.developersam.scheduler

import com.developersam.main.Database
import com.developersam.web.auth.FirebaseUser
import com.developersam.web.database.safeGetLong
import com.developersam.web.database.safeGetString
import com.google.cloud.datastore.Entity
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * [SchedulerItem] represents an editable item in the scheduler app.
 */
class SchedulerItem internal constructor(
        @field:Transient private val entity: Entity
) : Comparable<SchedulerItem> {

    /**
     * The key string of the entity.
     */
    private val keyString: String = entity.key.toUrlSafe()
    /**
     * Description of the item.
     */
    private val description: String = entity.getString("description")
    /**
     * Deadline of the item.
     */
    private val deadline: Date = entity.getTimestamp("deadline").toDate()
    /**
     * Deadline of the item with precision to hours,
     * which is completely optional.
     */
    private val deadlineHour: Int? = entity.safeGetLong("deadlineHour")?.toInt()
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
     * Whether the item has been completed.
     */
    private val isCompleted: Boolean = entity.getBoolean("completed")
    /**
     * The details of an item, which is completely optional.
     */
    private val detail: String? = entity.safeGetString("detail")

    /**
     * [belongsTo] reports whether the [SchedulerItem] belongs to another
     * [user].
     */
    internal fun belongsTo(user: FirebaseUser): Boolean =
            user.email == entity.getString("userEmail")

    /**
     * [markAs] marks the item as completed or not, as decided by [completed].
     * This operation does not check whether the operation is legal.
     * It is the client's responsibility to call [belongsTo] to ensure that.
     */
    internal fun markAs(completed: Boolean): Unit =
            Database.update(entity = entity, updater = { it.set("completed", completed) })

    override fun compareTo(other: SchedulerItem): Int {
        val c: Int = isCompleted.compareTo(other = other.isCompleted)
        return if (c == 0) totalHoursLeft.compareTo(other = other.totalHoursLeft) else c
    }

    companion object {

        /**
         * Construct a scheduler item fromKey a unique [key], which may fail due to invalid
         * key and return a `null`.
         */
        @JvmStatic
        fun fromKey(key: String): SchedulerItem? = Database[key]?.let { SchedulerItem(it) }

    }

}