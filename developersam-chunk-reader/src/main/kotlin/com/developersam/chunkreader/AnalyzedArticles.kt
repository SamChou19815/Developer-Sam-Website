package com.developersam.chunkreader

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.Query.FilterOperator.EQUAL
import com.google.appengine.api.datastore.Query.FilterPredicate
import com.google.appengine.api.datastore.Query.SortDirection.DESCENDING
import com.google.appengine.api.users.UserServiceFactory

/**
 * The object to obtain a list of [AnalyzedArticle] objects associated
 * with a currently logged in user.
 */
object AnalyzedArticles : DataStoreObject(kind = "ChunkReaderText") {

    /**
     * Obtain all analyzed articles objects related to a user.
     */
    val asList: List<AnalyzedArticle>
        get() {
            val query: Query = query
            query.filter = FilterPredicate("userEmail", EQUAL,
                    UserServiceFactory.getUserService().currentUser.email)
            query.addSort("date", DESCENDING)
            return dataStore.prepare(query).asIterable()
                    .map { AnalyzedArticle(entity = it, fullDetail = false) }
        }

}