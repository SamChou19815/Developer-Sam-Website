package com.developersam.web.model.scheduler

import com.developersam.web.util.DataStoreObject
import com.developersam.web.util.DateUtil
import com.developersam.web.util.dataStore
import com.developersam.web.util.getEntityByKey
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.users.UserServiceFactory
import java.util.Date

/**
 * A simple data class of scheduler item that can be easily written into
 * database. It is also a simplified object for JSON transmission.
 */
class SchedulerItemData
private constructor() : DataStoreObject(dataStoreTableName = "SchedulerItem") {

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
     * A helper method to check the sanity of the data and gives back an
     * [Entity] if it passes the check and `null` if not.
     */
    private fun sanityCheck(): Entity? {
        if (description == null || deadline == null
                || description.trim { it <= ' ' } == ""
                || deadline < DateUtil.yesterday) {
            return null
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
    fun writeToDatabase(): Boolean {
        val itemEntity = sanityCheck() ?: return false
        val userEmail = UserServiceFactory.getUserService().currentUser.email
        itemEntity.setProperty("userEmail", userEmail)
        itemEntity.setProperty("description", description)
        itemEntity.setProperty("deadline", deadline)
        itemEntity.setProperty("completed", false)
        dataStore.put(itemEntity)
        return true
    }

}
