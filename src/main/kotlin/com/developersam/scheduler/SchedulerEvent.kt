package com.developersam.scheduler

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typestore.TypedEntity
import typestore.TypedEntityCompanion
import typestore.TypedTable
import java.time.DayOfWeek

/**
 * [SchedulerEvent] represents an event that has a well-defined time interval and repeating config.
 *
 * @property key key to uniquely identify one event.
 * @property type type of the event.
 * @property title title of the event.
 * @property startHour starting hour of the event.
 * @property endHour ending hour of the event.
 * @property repeatConfig the config. for repeating.
 */
data class SchedulerEvent(
        override val key: Key? = null, val type: EventType = EventType.ONE_TIME,
        override val title: String = "",
        val startHour: Long = 0, val endHour: Long = 0, val repeatConfig: Long = 0
) : SchedulerRecord {

    /**
     * [isValid] checks and returns whether this [SchedulerEvent] is valid.
     */
    private val isValid: Boolean
        get() = when {
            title.isBlank() || startHour !in 0..23 || startHour >= endHour -> false
            type == EventType.ONE_TIME -> repeatConfig > System.currentTimeMillis() // Date valid
            type == EventType.WEEKLY -> Repeats.isValid(config = repeatConfig) // Valid repeat
            else -> error(message = "Impossible")
        }

    /**
     * [upsert] upserts this event for the [user] if the event is valid and belongs to the user.
     * It returns the key of the new event if it is successfully created and `null` if otherwise.
     */
    fun upsert(user: GoogleUser): Key? {
        if (!isValid) {
            return null
        }
        val entityOpt = key?.let { SchedulerEventEntity[it] }
        if (entityOpt != null && entityOpt.userId != user.uid) {
            return null // Illegal Access
        }
        return SchedulerEventEntity.upsert(entity = entityOpt) { t ->
            t[Table.userId] = user.uid
            t[Table.type] = type
            t[Table.title] = title
            t[Table.startHour] = startHour
            t[Table.endHour] = endHour
            t[Table.repeatConfig] = repeatConfig
        }.key
    }

    /**
     * [EventType] defines a set of supported repeat types.
     */
    enum class EventType {
        /**
         * [ONE_TIME] means a one-time event.
         * A one-time event's corresponding [repeatConfig] is simply the date.
         */
        ONE_TIME,
        /**
         * [WEEKLY] means a weekly event.
         * A weekly event's corresponding [repeatConfig] is a bit set that specifies which days
         * to repeat. The spec is in [Repeats].
         */
        WEEKLY
    }

    /**
     * [Repeats] defines a set of common repeat patterns.
     */
    object Repeats {
        /**
         * [SUNDAY] means repeating on Sunday.
         */
        private const val SUNDAY: Long = 1 shl 0
        /**
         * [MONDAY] means repeating on Monday.
         */
        private const val MONDAY: Long = 1 shl 1
        /**
         * [TUESDAY] means repeating on Tuesday.
         */
        private const val TUESDAY: Long = 1 shl 2
        /**
         * [WEDNESDAY] means repeating on Wednesday.
         */
        private const val WEDNESDAY: Long = 1 shl 3
        /**
         * [THURSDAY] means repeating on Thursday.
         */
        private const val THURSDAY: Long = 1 shl 4
        /**
         * [FRIDAY] means repeating on Friday.
         */
        private const val FRIDAY: Long = 1 shl 5
        /**
         * [SATURDAY] means repeating on Saturday.
         */
        private const val SATURDAY: Long = 1 shl 6

        /**
         * [WEEKDAYS] means repeating on weekdays.
         */
        private const val WEEKDAYS: Long = MONDAY or TUESDAY or WEDNESDAY or THURSDAY or FRIDAY
        /**
         * [WEEKENDS] means repeating on weekends.
         */
        private const val WEEKENDS: Long = SATURDAY or SUNDAY
        /**
         * [EVERYDAY] means repeating everyday.
         */
        private const val EVERYDAY: Long = WEEKDAYS or WEEKENDS

        /**
         * [isValid] returns whether the [config] represents a valid config.
         */
        @JvmStatic
        fun isValid(config: Long): Boolean = config != 0L && config or EVERYDAY == EVERYDAY

        /**
         * [DayOfWeek.inConfig] returns whether this number representing a [DayOfWeek] is presented
         * in config.
         */
        private fun Long.inConfig(config: Long): Boolean = this or config == config

        /**
         * [DayOfWeek.inConfig] returns whether this [DayOfWeek] is presented in config.
         */
        @JvmStatic
        fun DayOfWeek.inConfig(config: Long): Boolean = when (this) {
            DayOfWeek.SUNDAY -> SUNDAY.inConfig(config = config)
            DayOfWeek.MONDAY -> MONDAY.inConfig(config = config)
            DayOfWeek.TUESDAY -> TUESDAY.inConfig(config = config)
            DayOfWeek.WEDNESDAY -> WEDNESDAY.inConfig(config = config)
            DayOfWeek.THURSDAY -> THURSDAY.inConfig(config = config)
            DayOfWeek.FRIDAY -> FRIDAY.inConfig(config = config)
            DayOfWeek.SATURDAY -> SATURDAY.inConfig(config = config)
        }

    }

    /**
     * [Table] is the table definition for [SchedulerEvent].
     */
    private object Table : TypedTable<Table>(tableName = "SchedulerEvent") {
        val userId = stringProperty(name = "user_id")
        val type = enumProperty(name = "type", clazz = EventType::class.java)
        val title = stringProperty(name = "title")
        val startHour = longProperty(name = "start_hour")
        val endHour = longProperty(name = "end_hour")
        val repeatConfig = longProperty(name = "repeat_config")
    }

    private class SchedulerEventEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val userId: String = Table.userId.delegatedValue
        val type: EventType = Table.type.delegatedValue
        val title: String = Table.title.delegatedValue
        val startHour: Long = Table.startHour.delegatedValue
        val endHour: Long = Table.endHour.delegatedValue
        val repeatConfig: Long = Table.repeatConfig.delegatedValue

        val asSchedulerEvent: SchedulerEvent
            get() = SchedulerEvent(
                    key = key, type = type, title = title,
                    startHour = startHour, endHour = endHour, repeatConfig = repeatConfig
            )

        companion object : TypedEntityCompanion<Table, SchedulerEventEntity>(table = Table) {
            override fun create(entity: Entity): SchedulerEventEntity = SchedulerEventEntity(entity)
        }

    }

    companion object {

        /**
         * [get] returns a list of [SchedulerEvent] for a given [user].
         *
         * @param user the user whose events need to be fetched. This user must exist.
         */
        internal operator fun get(user: GoogleUser): List<SchedulerEvent> =
                SchedulerEventEntity.query { filter = Table.userId eq user.uid }
                        .map { it.asSchedulerEvent }
                        .toList()

        /**
         * [delete] removes a scheduler event from database with a given [key] if the event really
         * belongs to the given [user].
         */
        fun delete(user: GoogleUser, key: Key) {
            SchedulerEventEntity[key]?.takeIf { it.userId == user.uid }
                    ?.let { SchedulerEventEntity.delete(it) }
        }

    }

}
