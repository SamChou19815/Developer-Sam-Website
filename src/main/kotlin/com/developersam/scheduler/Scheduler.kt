package com.developersam.scheduler

import com.developersam.fp.FpList
import com.developersam.fp.FpMap
import com.developersam.fp.cons

/**
 * [Scheduler] is responsible for providing algorithms to automatically schedule a list of selected
 * tasks based on user supplied items.
 *
 * @param config1 the config for the first person, which is treated as the primary user.
 * @param config2 the config for the second person, which can be omitted and then this class can be
 * used to do scheduling for only one person.
 */
class Scheduler(config1: List<SchedulerItem>, config2: List<SchedulerItem> = emptyList()) {

    /*
     * --------------------------------------------------------------------------------
     * Part 0: Initializations
     * --------------------------------------------------------------------------------
     */

    /**
     * [preparedIntervals] is a list of all generated intervals sorted by the finishing time.
     */
    private val preparedIntervals: List<AnnotatedInterval> = Merger(config1, config2).merge()
            .asSequence()
            .map(AnnotatedItem::generateIntervals)
            .flatten()
            .sorted()
            .toList()

    /*
     * --------------------------------------------------------------------------------
     * Part 1: Scheduler Item Extensions
     * --------------------------------------------------------------------------------
     */

    /**
     * [SchedulerItem.getTotalWeight] returns the total weight of the scheduler item with
     * count [timeUnitCount].
     */
    fun SchedulerItem.getTotalWeight(timeUnitCount: Int): Double {
        if (timeUnitCount <= estimatedTimeUnits) {
            return (timeUnitCount * weight).toDouble()
        }
        var decayCoefficient = DECAY_RATE
        var totalWeight = (estimatedTimeUnits * weight).toDouble()
        for (i in 0 until (timeUnitCount - estimatedTimeUnits)) {
            totalWeight += decayCoefficient * weight
            decayCoefficient *= DECAY_RATE
        }
        return totalWeight
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 2: Class Definitions
     * --------------------------------------------------------------------------------
     */

    /**
     * [AnnotatedInterval] represents an interval with [start] and [end], with additional fields
     * such as [key], [item] to link to a backing [SchedulerItem], user identity marker
     * [isPrimaryUser].
     */
    private data class AnnotatedInterval(
            val key: String, val item: SchedulerItem,
            val isPrimaryUser: Boolean, val start: Long, val end: Long
    ) : Comparable<AnnotatedInterval> {

        override fun compareTo(other: AnnotatedInterval): Int = start.compareTo(other = end)

    }

    /**
     * [AnnotatedItem] is the scheduler item with string based [key] and user identity marker
     * [isPrimaryUser].
     */
    private data class AnnotatedItem(
            val key: String, val isPrimaryUser: Boolean, val original: SchedulerItem
    ) {

        /**
         * Construct from the [original] item and [isPrimaryUser] marker directly.
         * The [original] one is assumed to have a non-null key.
         */
        constructor(isPrimaryUser: Boolean, original: SchedulerItem) : this(
                key = original.key?.toUrlSafe()!!, isPrimaryUser = isPrimaryUser,
                original = original
        )

        /**
         * [generateIntervals] returns a list of intervals between now and the deadline of the item.
         */
        fun generateIntervals(): List<AnnotatedInterval> {
            val minTimeUnits = 1000 * 3600 * original.minimumTimeUnits
            val now = System.currentTimeMillis()
            var end = original.deadline
            val list = ArrayList<AnnotatedInterval>(((end - now) / minTimeUnits + 1).toInt())
            while (end > now) {
                val start = end - minTimeUnits
                list.add(element = AnnotatedInterval(
                        key = key, item = original, isPrimaryUser = isPrimaryUser,
                        start = start, end = end
                ))
                end = start
            }
            list.reverse()
            return list
        }

        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            other is AnnotatedItem -> key == other.key
            else -> false
        }

        override fun hashCode(): Int = key.hashCode()

    }

    /**
     * [Plan] is a record of a plan with a list of [intervalList].
     * It also has a [timeUnitStat] and [totalWeight] associated to the list. These values must be
     * in sync.
     */
    private inner class Plan(
            val intervalList: FpList<AnnotatedInterval> = FpList.empty(),
            val timeUnitStat: FpMap<String, Int> = FpMap.empty(), val totalWeight: Double = 0.0
    ) {

        /**
         * Construct with an [interval] only.
         */
        constructor(interval: AnnotatedInterval) : this(
                intervalList = FpList.singleton(data = interval),
                timeUnitStat = FpMap.singleton(key = interval.key, value = 1),
                totalWeight = interval.item.weight.toDouble()
        )

        /**
         * [canAdd] reports whether the given [interval] can be added to this plan.
         */
        private fun canAdd(interval: AnnotatedInterval): Boolean = when (intervalList) {
            FpList.Nil -> true
            is FpList.Node<AnnotatedInterval> -> intervalList.data.end <= interval.start
        }

        /**
         * [with] returns the plan with the given [interval] if it can be added, `null` if cannot.
         */
        fun with(interval: AnnotatedInterval): Plan? {
            if (!canAdd(interval = interval)) {
                return null
            }
            val newList = interval cons intervalList
            val key = interval.key
            val oldCount = timeUnitStat[key] ?: 0
            val newCount = oldCount + 1
            val newStat = timeUnitStat.put(key = key, value = newCount)
            val item = interval.item
            val newWeight = totalWeight - item.getTotalWeight(timeUnitCount = oldCount) +
                    item.getTotalWeight(timeUnitCount = newCount)
            return Plan(intervalList = newList, timeUnitStat = newStat, totalWeight = newWeight)
        }

    }

    /**
     * [PlanPair] is a pair of plans where [p1] is for user 1 and [p2] is for user 2.
     */
    private inner class PlanPair(val p1: Plan = Plan(), val p2: Plan = Plan()) {

        /**
         * [totalWeight] returns the total weight of the plan.
         */
        val totalWeight: Double get() = p1.totalWeight + p2.totalWeight

        /**
         * [with] returns the plan pair with the given [interval] if it can be added, `null` if
         * cannot.
         */
        fun with(interval: AnnotatedInterval): PlanPair? {
            return when {
                interval.item.isGroupProject -> {
                    val newP1 = p1.with(interval = interval) ?: return null
                    val newP2 = p2.with(interval = interval) ?: return null
                    PlanPair(p1 = newP1, p2 = newP2)
                }
                interval.isPrimaryUser -> {
                    val newP1 = p1.with(interval = interval) ?: return null
                    PlanPair(p1 = newP1, p2 = p2)
                }
                else -> {
                    val newP2 = p2.with(interval = interval) ?: return null
                    PlanPair(p1 = p1, p2 = newP2)
                }
            }
        }

    }

    /**
     * [createPlanPair] returns an created [PlanPair] from a single [interval].
     */
    private fun createPlanPair(interval: AnnotatedInterval): PlanPair {
        val plan = Plan(interval = interval)
        return when {
            interval.item.isGroupProject -> PlanPair(p1 = plan, p2 = plan)
            interval.isPrimaryUser -> PlanPair(p1 = plan, p2 = Plan())
            else -> PlanPair(p1 = Plan(), p2 = plan)
        }
    }

    /**
     * [Interval] is a simple data class that represents an interval.
     */
    data class Interval(val start: Long, val end: Long)

    /**
     * [IntervalsAnnotatedItem] represents a scheduler item annotated by a list of sorted,
     * not-completed, non-conflicting intervals that is recommended by the system.
     */
    data class IntervalsAnnotatedItem(val item: SchedulerItem, val intervals: List<Interval>)

    /*
     * --------------------------------------------------------------------------------
     * Part 3: Pre-processors
     * --------------------------------------------------------------------------------
     */

    /**
     * [Merger] is responsible for merge the raw data [config1] and [config2].
     */
    private class Merger(val config1: List<SchedulerItem>, val config2: List<SchedulerItem>) {

        /**
         * [personalItems] is the list of personal items.
         */
        private val personalItems = arrayListOf<AnnotatedItem>()
        /**
         * [tentativeGroupProjects] is a list of possibly group projects.
         */
        private val tentativeGroupProjects = arrayListOf<AnnotatedItem>()
        /**
         * [realGroupProjects] is a list of confirmed common group projects.
         */
        private val realGroupProjects = arrayListOf<AnnotatedItem>()

        /**
         * [classifyList] transforms and adds items in config to [personalItems] or
         * [tentativeGroupProjects] based on items' properties.
         */
        private fun classifyList(config: List<SchedulerItem>, isPrimaryUser: Boolean) {
            for (item in config) {
                val annotatedItem = AnnotatedItem(isPrimaryUser = isPrimaryUser, original = item)
                if (item.isGroupProject) {
                    tentativeGroupProjects.add(element = annotatedItem)
                } else {
                    personalItems.add(element = annotatedItem)
                }
            }
        }

        /**
         * [average] returns the average of [fo] and [so] based on [transform].
         */
        private inline fun average(
                fo: SchedulerItem, so: SchedulerItem,
                crossinline transform: SchedulerItem.() -> Long
        ): Long = (fo.transform() + so.transform()) / 2

        /**
         * [reconcile] tries to reconcile the difference between items in a title group.
         */
        private fun reconcile(items: List<AnnotatedItem>) {
            when (items.size) {
                0, 1 -> {
                    // Not common for both
                    for (item in items) {
                        val correctItem = item.copy(original = item.original.copy(
                                isGroupProject = false
                        ))
                        personalItems.add(element = correctItem)
                    }
                }
                2 -> {
                    val i1 = items[0]
                    val i2 = items[1]
                    val first = if (i1.isPrimaryUser) i1 else i2
                    val second = if (i1.isPrimaryUser) i2 else i1
                    val fo = first.original
                    val so = second.original
                    val reconciled = fo.copy(
                            minimumTimeUnits = average(fo, so) { minimumTimeUnits },
                            estimatedTimeUnits = average(fo, so) { estimatedTimeUnits },
                            weight = average(fo, so) { weight }
                    )
                    realGroupProjects.add(element = AnnotatedItem(
                            isPrimaryUser = true, original = reconciled
                    ))
                }
                else -> error(message = "Impossible")
            }
        }

        /**
         * [merge] tries to merge two config [config1] [config2] into one. It will detect the
         * difference and use average or relying on [config1] to resolve differences.
         *
         * @return a merged config.
         */
        fun merge(): List<AnnotatedItem> {
            classifyList(config = config1, isPrimaryUser = true)
            classifyList(config = config2, isPrimaryUser = false)
            val titleGroupedGroupProjects = tentativeGroupProjects.asSequence()
                    .groupBy { it.original.title }.map { it.value }
            titleGroupedGroupProjects.forEach { reconcile(items = it) }
            return personalItems.apply { addAll(realGroupProjects) }
        }
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 4: Public APIs
     * --------------------------------------------------------------------------------
     */

    /**
     * [schedule] returns a list of [IntervalsAnnotatedItem] for a group in the perspective
     * of the primary user.
     */
    fun schedule(): List<IntervalsAnnotatedItem> {
        val len = preparedIntervals.size
        // dp array
        val dp: Array<PlanPair> = Array(size = len) { PlanPair() }
        for (i in 0 until len) {
            val interval = preparedIntervals[i]
            var optPlanPair = createPlanPair(interval = interval) // singleton plan
            var currentBestWeight = optPlanPair.totalWeight
            for (j in 0 until i) {
                // combined with existing opt-plans
                val anotherPlanPair = dp[j].with(interval = interval) ?: continue
                val anotherPlanWeight = anotherPlanPair.totalWeight
                if (anotherPlanWeight > currentBestWeight) {
                    currentBestWeight = anotherPlanWeight
                    optPlanPair = anotherPlanPair
                }
            }
            dp[i] = optPlanPair
        }
        // extract result
        return dp[len - 1].p1.intervalList.reverse
                .groupBy { it.key }
                .map { (_, intervals) ->
                    IntervalsAnnotatedItem(
                            item = intervals[0].item,
                            intervals = intervals.map { Interval(start = it.start, end = it.end) }
                    )
                }
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 5: Constants
     * --------------------------------------------------------------------------------
     */

    /**
     * [Constants] stores a list of constants.
     */
    private companion object Constants {

        /**
         * [DECAY_RATE] represents the rate of weight decay when the estimated number of units has
         * been surpassed.
         */
        private const val DECAY_RATE: Double = 2.0 / 3

    }

}
