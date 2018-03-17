package com.developersam.chunkreader.summary

import com.developersam.util.runQueryOf
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.OrderBy.desc
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor

/**
 * Commonly used kind of the entities.
 */
private const val kind = "ChunkReaderTextSummary"
/**
 * Commonly used order.
 */
private val orderBy = desc("salience")

/**
 * [RetrievedSummaries] is used to fetch a list of summaries for a common text
 * key for a given limit (defaults to 5), which will be auto-corrected to 1 if
 * it is below 1.
 */
class RetrievedSummaries internal constructor(
        private val textKey: Key, limit: Int = 5
) {

    /**
     * The actual limit applied.
     */
    private val actualLimit = if (limit > 0) limit else 1
    private val filter = hasAncestor(textKey)

    /**
     * Fetch a list of sentences in pure string form associated with the text
     * key given in constructor.
     */
    val asList: List<String> =
            runQueryOf(kind, filter, orderBy, actualLimit)
                    .map { AnnotatedSentence(entity = it) }
                    .sortedBy { it.beginOffset }
                    .map { it.sentence }
                    .toList()

    companion object {

        /**
         * Construct a [RetrievedSummaries] from a [SummaryRequest], which may
         * fail and return `null` due to bad key value.
         */
        fun from(summaryRequest: SummaryRequest): RetrievedSummaries? {
            val keyString = summaryRequest.keyString ?: return null
            val key = Key.fromUrlSafe(keyString)
            return RetrievedSummaries(
                    textKey = key, limit = summaryRequest.limit
            )
        }

    }
}