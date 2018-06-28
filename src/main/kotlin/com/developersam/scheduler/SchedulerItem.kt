package com.developersam.scheduler

import com.developersam.auth.FirebaseUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typestore.TypedEntity
import typestore.TypedEntityCompanion
import typestore.TypedTable
import typestore.defaultDatastore
import typestore.toLocalDateTimeInUTC
import typestore.toUTCMillis
import typestore.transaction
import java.time.LocalDateTime

/**
 * [SchedulerItem] defines a set of things recorded for a scheduler item.
 *
 * @property key key to uniquely identify one item.
 * @property title title of the item.
 * @property deadline deadline of the item in long.
 * @property isCompleted whether this item
 */
data class SchedulerItem(
        private val key: Key? = null, private val title: String = "",
        private val deadline: Long = 0, private val isCompleted: Boolean = false,
        private val detail: String = ""
) {

    private val isValid: Boolean get() = title.isNotBlank() && deadline > System.currentTimeMillis()

    fun upsert(user: FirebaseUser): Key? {
        if (!isValid) {
            return null
        }
        val entityOpt = key?.let { SchedulerItemEntity[it] }
        if (entityOpt != null && entityOpt.userId != user.uid) {
            return null // Illegal Access
        }
        return SchedulerItemEntity.upsert(entity = entityOpt) { t ->
            t[Table.userId] = user.uid
            t[Table.title] = title
            t[Table.deadline] = toLocalDateTimeInUTC(date = deadline)
            t[Table.isCompleted] = isCompleted
            t[Table.detail] = detail
        }.key
    }

    /**
     * [Table] is the table definition for [SchedulerItem].
     */
    private object Table : TypedTable<Table>(tableName = "SchedulerItem") {
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
            TypedEntity<Table>(entity = entity) {
        val userId: String = Table.userId.delegatedValue
        val title: String = Table.title.delegatedValue
        val deadline: LocalDateTime = Table.deadline.delegatedValue
        val isCompleted: Boolean = Table.isCompleted.delegatedValue
        val detail: String = Table.detail.delegatedValue

        val asSchedulerItem: SchedulerItem
            get() = SchedulerItem(
                    key = key, title = title, deadline = deadline.toUTCMillis(),
                    isCompleted = isCompleted, detail = detail
            )

        companion object : TypedEntityCompanion<Table, SchedulerItemEntity>(table = Table) {
            override fun create(entity: Entity): SchedulerItemEntity =
                    SchedulerItemEntity(entity = entity)
        }
    }

    companion object {

        /**
         * [get] returns a list of [SchedulerItem] for a given [user].
         *
         * @param user the user whose items need to be fetched. This user must exist.
         */
        operator fun get(user: FirebaseUser): List<SchedulerItem> =
                SchedulerItemEntity.query {
                    filter = (Table.userId eq user.uid) and Table.deadline.isFuture()
                    order = Table.deadline.asc()
                }.map { it.asSchedulerItem }.toList()

        /**
         * [markAs] marks the completion status for a scheduler item specified
         * by the given [key] and the desired new completion status [isCompleted] if
         * the item really belongs to the given [user].
         */
        fun markAs(user: FirebaseUser, key: Key, isCompleted: Boolean) {
            defaultDatastore.transaction {
                SchedulerItemEntity[key]?.let { item ->
                    if (item.userId == user.uid) {
                        SchedulerItemEntity.update(item) { t ->
                            t[Table.isCompleted] = isCompleted
                        }
                    }
                }
            }
        }

        /**
         * [delete] removes a scheduler item from database with a given [key] if
         * the item really belongs to the given [user].
         */
        fun delete(user: FirebaseUser, key: Key) {
            SchedulerItemEntity[key]?.let { item ->
                if (item.userId == user.uid) {
                    SchedulerItemEntity.delete(item)
                }
            }
        }

    }

}
