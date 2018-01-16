package com.developersam.model.chunkreader.category

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
class RetrievedCategories(textKey: Key) :
        DataStoreObject(kind = "ChunkReaderContentCategory", parent = textKey) {

    /**
     * Fetch a list of [Category] objects associated with the text key given
     * in constructor.
     */
    val asList: List<Category>
        get() {
            val query: Query = query
            query.addSort("confidence", DESCENDING)
            val pq: PreparedQuery = dataStore.prepare(query)
            return pq.asList(FetchOptions.Builder.withLimit(3)).map {
                Category.from(it)
            }
        }

}