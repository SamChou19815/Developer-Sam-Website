package com.developersam.scheduler

/**
 * [TaggedInterval] is a simple data class that represents an interval with [start] and [end] and
 * a [type] and [title] for that interval.
 */
data class TaggedInterval(
        val type: Type, val title: String, val start: Long, val end: Long
) : Comparable<TaggedInterval> {

    /**
     * [Type] is a collection of supported types.
     */
    enum class Type {
        /**
         * Project Type.
         */
        PROJECT,
        /**
         * Event Type.
         */
        EVENT;
    }

    override fun compareTo(other: TaggedInterval): Int = end.compareTo(other = end)

}