package com.developersam.scheduler

import com.developersam.typestore.TypedEntity
import com.developersam.typestore.TypedEntityCompanion
import com.developersam.typestore.TypedTable
import com.developersam.typestore.defaultDatastore
import com.developersam.typestore.toLocalDateTimeInUTC
import com.developersam.typestore.toUTCMillis
import com.developersam.typestore.transaction
import com.developersam.web.auth.FirebaseUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import java.time.LocalDateTime

/**
 * [SchedulerItemTable] is the table definition for [SchedulerItem].
 */
private object SchedulerItemTable : TypedTable<SchedulerItemTable>() {
    val userId = stringProperty(name = "user_id")
    val title = stringProperty(name = "title")
    val deadline = datetimeProperty(name = "deadline")
    val isCompleted = boolProperty(name = "completed")
    val detail = longStringProperty(name = "detail")
}

/**
 * [SchedulerItemEntity] is the entity definition for [SchedulerItem].
 */
private class SchedulerItemEntity(entity: Entity) :
        TypedEntity<SchedulerItemTable>(entity = entity) {
    val userId: String = SchedulerItemTable.userId.delegatedValue
    val title: String = SchedulerItemTable.title.delegatedValue
    val deadline: LocalDateTime = SchedulerItemTable.deadline.delegatedValue
    val isCompleted: Boolean = SchedulerItemTable.isCompleted.delegatedValue
    val detail: String = SchedulerItemTable.detail.delegatedValue

    val asSchedulerItem: SchedulerItem
        get() = SchedulerItem(
                key = key.toUrlSafe(), title = title, deadline = deadline.toUTCMillis(),
                isCompleted = isCompleted, detail = detail
        )

    companion object : TypedEntityCompanion<SchedulerItemTable, SchedulerItemEntity>(
            table = SchedulerItemTable
    ) {
        override fun create(entity: Entity): SchedulerItemEntity =
                SchedulerItemEntity(entity = entity)
    }
}

/**
 * [SchedulerItem] defines a set of things recorded for a scheduler item.
 *
 * @property key key to uniquely identify one item.
 * @property title title of the item.
 * @property deadline deadline of the item in long.
 * @property isCompleted whether this item
 */
data class SchedulerItem(
        val key: String? = null, val title: String = "", val deadline: Long = 0,
        val isCompleted: Boolean = false, val detail: String = ""
) {

    private val isValid: Boolean
        get() = title.isNotBlank() && deadline > System.currentTimeMillis()

    fun upsert(user: FirebaseUser): Key? {
        if (!isValid) {
            return null
        }
        val entityOpt = key?.let { SchedulerItemEntity[Key.fromUrlSafe(it)] }
        if (entityOpt != null && entityOpt.userId != user.uid) {
            return null // Illegal Access
        }
        return SchedulerItemEntity.upsert(entity = entityOpt) { t ->
            t[SchedulerItemTable.userId] = user.uid
            t[SchedulerItemTable.title] = title
            t[SchedulerItemTable.deadline] = toLocalDateTimeInUTC(date = deadline)
            t[SchedulerItemTable.isCompleted] = isCompleted
            t[SchedulerItemTable.detail] = detail
        }.key
    }

    companion object {

        /**
         * [get] returns a list of [SchedulerItem] for a given [user].
         *
         * @param user the user whose items need to be fetched. This user must exist.
         */
        operator fun get(user: FirebaseUser): List<SchedulerItem> =
                SchedulerItemEntity.query {
                    filter = (SchedulerItemTable.userId eq user.uid) and
                            SchedulerItemTable.deadline.isFuture()
                    order = SchedulerItemTable.deadline.asc()
                }.map { it.asSchedulerItem }.toList()

        /**
         * [markAs] marks the completion status for a scheduler item specified
         * by the given [key] and the desired new completion status [isCompleted] if
         * the item really belongs to the given [user].
         */
        fun markAs(user: FirebaseUser, key: String, isCompleted: Boolean) {
            defaultDatastore.transaction {
                SchedulerItemEntity[Key.fromUrlSafe(key)]?.let { item ->
                    if (item.userId == user.uid) {
                        SchedulerItemEntity.update(item) { t ->
                            t[SchedulerItemTable.isCompleted] = isCompleted
                        }
                    }
                }
            }
        }

        /**
         * [delete] removes a scheduler item from database with a given [key] if
         * the item really belongs to the given [user].
         */
        fun delete(user: FirebaseUser, key: String) {
            SchedulerItemEntity[Key.fromUrlSafe(key)]?.let { item ->
                if (item.userId == user.uid) {
                    SchedulerItemEntity.delete(item)
                }
            }
        }

    }

}
