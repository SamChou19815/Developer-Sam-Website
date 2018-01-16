package com.developersam.model.chunkreader.summary

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.FetchOptions
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.SortDirection.DESCENDING

/**
 * The class used to fetch a list of summaries for a common text key for
 * a given [limit], which will be auto-corrected to 1 if it is below 1.
 */
class RetrievedSummaries(textKey: Key, private val limit: Int) :
        DataStoreObject(kind = "ChunkReaderTextSummary", parent = textKey) {

    /**
     * Fetch a list of [AnnotatedSentence] objects associated with the text
     * key given in constructor.
     */
    val asList: List<AnnotatedSentence>
        get() {
            val query: Query = query
            query.addSort("salience", DESCENDING)
            val limit = if (this.limit > 0) limit else 1
            return dataStore.prepare(query)
                    .asList(FetchOptions.Builder.withLimit(limit))
                    .asSequence()
                    .map { entity -> AnnotatedSentence(entity = entity) }
                    .sortedBy { it.beginOffset }
                    .toList()
        }

}