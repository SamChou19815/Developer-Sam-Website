package com.developersam.scheduler

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.Writable
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.datastore.getEntityByKey
import com.developersam.webcore.date.yesterday
import com.developersam.webcore.exception.AccessDeniedException
import com.developersam.webcore.service.GoogleUserService
import com.google.appengine.api.datastore.Entity
import com.sun.codemodel.internal.JOp.xor
import java.util.Date

/**
 * A simple data class of scheduler item that can be easily written into
 * database. It is also a simplified object for JSON transmission.
 */
class SchedulerItemData private constructor() :
        DataStoreObject(kind = "SchedulerItem"),
        Writable {

    /**
     * An optional field that that essentially tells the difference between
     * a new scheduler item or an old one.
     */
    private val keyString: String? = null
    /**
     * Description of the item.
     */
    private val description: String? = null
    /**
     * Deadline of the item.
     */
    private val deadline: Date? = null
    /**
     * The optional deadline hour.
     */
    private val deadlineHour: Int? = null
    /**
     * The optional estimated hours for the project.
     */
    private val estimatedHours: Int? = null
    /**
     * The optional estimated progress for the project, which is always between
     * 0 and 100. If it reaches 100, it should be marked as completed
     * automatically.
     */
    private val estimatedProgress: Int? = null;
    /**
     * Optional detail of the item.
     */
    private var detail: String? = null

    /**
     * A helper method to check the sanity of the data and gives back an
     * [Entity] if it passes the check and `null` if not.
     */
    private fun sanityCheck(): Entity? {
        if (description == null || deadline == null
                || description.trim().isEmpty()
                || deadline < yesterday) {
            return null
        }
        if (deadlineHour != null && deadlineHour !in 1..24) {
            // Deadline hours must be in range.
            return null
        }
        if (estimatedHours != null && estimatedHours <= 0
                && estimatedProgress == null) {
            // Estimated hours must be positive.
            return null
        }
        if (!((estimatedHours != null) xor (estimatedProgress == null))) {
            // When est. hours is provided,
            // est. progress should auto be provided and vice versa.
            return null
        }
        if (estimatedProgress != null && estimatedProgress !in 0..100) {
            // Estimated progress must be in range.
            return null;
        }
        return if (keyString == null) {
            newEntity
        } else {
            dataStore.getEntityByKey(keyString)
        }
    }

    /**
     * Write the current record into the database, if it passed the sanity
     * check and tells whether it was successful.
     */
    override fun writeToDatabase(): Boolean {
        val itemEntity = sanityCheck() ?: return false
        val userEmail = GoogleUserService.currentUser?.email
                ?: throw AccessDeniedException()
        itemEntity.setProperty("userEmail", userEmail)
        itemEntity.setProperty("description", description)
        itemEntity.setProperty("deadline", deadline)
        itemEntity.setProperty("deadlineHour", deadlineHour)
        itemEntity.setProperty("estimatedHours", estimatedHours)
        itemEntity.setProperty("estimatedProgress", estimatedProgress)
        // Automatic setting of completion
        itemEntity.setProperty("completed", estimatedProgress == 100)
        // Don't record meaningless detail.
        detail = detail?.trim()
        if (detail?.isEmpty() == true) {
            detail = null
        }
        itemEntity.setProperty("detail", detail)
        dataStore.put(itemEntity)
        return true
    }

}
