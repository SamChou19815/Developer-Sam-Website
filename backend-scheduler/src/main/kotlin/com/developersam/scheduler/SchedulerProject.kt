package com.developersam.scheduler

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable
import typedstore.defaultDatastore
import typedstore.toLocalDateTimeInUTC
import typedstore.toUTCMillis
import typedstore.transaction
import java.time.LocalDateTime

/**
 * [SchedulerProject] defines a set of things recorded for a scheduler project.
 *
 * @property key key to uniquely identify one project.
 * @property title title of the project.
 * @property deadline deadline of the project in long.
 * @property isCompleted whether this project is completed.
 * @property minimumTimeUnits minimum discrete time units that can be spent on this task.
 * @property estimatedTimeUnits the estimated number of time units to spend on this task.
 * @property isGroupProject whether the project is a group project.
 * @property detail the optional detail of the project.
 */
data class SchedulerProject(
        override val key: Key? = null, override val title: String = "", val deadline: Long = 0,
        val isCompleted: Boolean = false, private val detail: String = "",
        val minimumTimeUnits: Long = 0, val estimatedTimeUnits: Long = 0,
        val isGroupProject: Boolean = false, val weight: Long = 0
) : SchedulerRecord {

    /**
     * [isValid] checks and returns whether this [SchedulerProject] is valid.
     */
    private val isValid: Boolean
        get() = title.isNotBlank() && deadline > System.currentTimeMillis() &&
                minimumTimeUnits in 1..5 && estimatedTimeUnits in 1..20 && weight in 1..10

    /**
     * [upsert] upserts this project for the [user] if the project is valid and belongs to the user.
     * It returns the key of the new project if it is successfully created and `null` if otherwise.
     */
    fun upsert(user: GoogleUser): Key? {
        if (!isValid) {
            return null
        }
        val entityOpt = key?.let { SchedulerItemEntity[it] }
        if (entityOpt != null && entityOpt.userId != user.uid) {
            return null // Illegal Access
        }
        return SchedulerItemEntity.upsert(entity = entityOpt) {
            table.userId gets user.uid
            table.title gets title
            table.deadline gets toLocalDateTimeInUTC(date = deadline)
            table.isCompleted gets isCompleted
            table.detail gets detail
            table.minimumTimeUnits gets minimumTimeUnits
            table.estimatedTimeUnits gets estimatedTimeUnits
            table.isGroupProject gets isGroupProject
            table.weight gets weight
        }.key
    }

    /**
     * [Table] is the table definition for [SchedulerProject].
     */
    private object Table : TypedTable<Table>(tableName = "SchedulerProject") {
        val userId = stringProperty(name = "user_id")
        val title = stringProperty(name = "title")
        val deadline = datetimeProperty(name = "deadline")
        val isCompleted = boolProperty(name = "completed")
        val detail = longStringProperty(name = "detail")
        val minimumTimeUnits = longProperty(name = "min_time_units")
        val estimatedTimeUnits = longProperty(name = "est_time_units")
        val isGroupProject = boolProperty(name = "group_project")
        val weight = longProperty(name = "weight")
    }

    /**
     * [SchedulerItemEntity] is the entity definition for [SchedulerProject].
     */
    private class SchedulerItemEntity(entity: Entity) :
            TypedEntity<Table>(entity = entity) {
        val userId: String = Table.userId.delegatedValue
        val title: String = Table.title.delegatedValue
        val deadline: LocalDateTime = Table.deadline.delegatedValue
        val isCompleted: Boolean = Table.isCompleted.delegatedValue
        val detail: String = Table.detail.delegatedValue
        val minimumTimeUnits = Table.minimumTimeUnits.delegatedValue
        val estimatedTimeUnits = Table.estimatedTimeUnits.delegatedValue
        val isGroupProject = Table.isGroupProject.delegatedValue
        val weight = Table.weight.delegatedValue

        val asSchedulerProject: SchedulerProject
            get() = SchedulerProject(
                    key = key, title = title, deadline = deadline.toUTCMillis(),
                    isCompleted = isCompleted, detail = detail,
                    minimumTimeUnits = minimumTimeUnits, estimatedTimeUnits = estimatedTimeUnits,
                    isGroupProject = isGroupProject, weight = weight
            )

        companion object : TypedEntityCompanion<Table, SchedulerItemEntity>(table = Table) {
            override fun create(entity: Entity): SchedulerItemEntity =
                    SchedulerItemEntity(entity = entity)
        }
    }

    companion object {

        /**
         * [get] returns a list of [SchedulerProject] for a given [user], sorted by deadline.
         *
         * @param user the user whose projects need to be fetched. This user must exist.
         */
        internal operator fun get(user: GoogleUser): List<SchedulerProject> =
                SchedulerItemEntity.query {
                    filter {
                        table.userId eq user.uid
                        table.deadline.isFuture()
                    }
                    order { table.deadline.asc() }
                }.map { it.asSchedulerProject }.toList()

        /**
         * [markAs] marks the completion status for a scheduler project specified by the given [key]
         * and the desired new completion status [isCompleted] if the project really belongs to the
         * given [user].
         */
        fun markAs(user: GoogleUser, key: Key, isCompleted: Boolean) {
            defaultDatastore.transaction {
                SchedulerItemEntity[key]?.let { item ->
                    if (item.userId == user.uid) {
                        SchedulerItemEntity.update(item) { table.isCompleted gets isCompleted }
                    }
                }
            }
        }

        /**
         * [delete] removes a scheduler project from database with a given [key] if the project
         * really belongs to the given [user].
         */
        fun delete(user: GoogleUser, key: Key) {
            SchedulerItemEntity[key]?.takeIf { it.userId == user.uid }
                    ?.let { SchedulerItemEntity.delete(key) }
        }

    }

}
