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

    internal companion object {

        /**
         * [mergeAdjacentInterval] will merge adjacent intervals for the same item in the given
         * sorted list of [intervals].
         */
        fun mergeAdjacentInterval(intervals: List<TaggedInterval>): List<TaggedInterval> {
            val size = intervals.size
            if (size == 0) {
                return emptyList()
            }
            val newList = arrayListOf<TaggedInterval>()
            var previousInterval = intervals[0]
            for (i in 1 until size) {
                val currentInterval = intervals[i]
                previousInterval = if (currentInterval.title == previousInterval.title &&
                        currentInterval.type == previousInterval.type &&
                        currentInterval.start == previousInterval.end) {
                    // Can merge
                    previousInterval.copy(end = currentInterval.end)
                } else {
                    // Push previous one to list and make this the new previous one.
                    newList.add(element = previousInterval)
                    currentInterval
                }
            }
            newList.add(element = previousInterval)
            return newList
        }

    }

}