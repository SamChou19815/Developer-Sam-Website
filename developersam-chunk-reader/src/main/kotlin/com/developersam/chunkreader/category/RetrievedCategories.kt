package com.developersam.chunkreader.category

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.FetchOptions
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.SortDirection.DESCENDING

/**
 * The class used to fetch a list of categories for a common text key.
 */
internal class RetrievedCategories(textKey: Key) :
        DataStoreObject(kind = "ChunkReaderContentCategory", parent = textKey) {

    /**
     * Fetch a list of categories in string form associated with the text key
     * given in constructor.
     */
    val asList: List<String>
        get() {
            val query: Query = query
            query.addSort("confidence", DESCENDING)
            val pq: PreparedQuery = dataStore.prepare(query)
            return pq.asList(FetchOptions.Builder.withLimit(3)).map {
                Category.fromEntity(entity = it).name
            }
        }

}