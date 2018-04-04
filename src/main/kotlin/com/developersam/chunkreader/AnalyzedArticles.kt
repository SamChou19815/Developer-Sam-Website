package com.developersam.chunkreader

import com.developersam.database.DatastoreClient
import com.developersam.util.Consumer
import com.developersam.util.consumeBy
import com.developersam.web.auth.FirebaseUser
import com.google.cloud.datastore.StructuredQuery.OrderBy.desc
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.eq

/**
 * The object to obtain a list of [AnalyzedArticle] objects associated
 * with a user.
 */
object AnalyzedArticles {

    /**
     * [get] gives a list of analyzed articles of a user in [consumer].
     */
    fun get(user: FirebaseUser, consumer: Consumer<List<AnalyzedArticle>>) =
            DatastoreClient.query(
                    kind = "ChunkReaderText",
                    filter = eq("userEmail", user.email),
                    orderBy = desc("date")
            ) { s ->
                s.map { AnalyzedArticle(entity = it, fullDetail = false) }
                        .toList().consumeBy(consumer = consumer)
            }

}
