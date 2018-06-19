package com.developersam.chunkreader

import com.developersam.main.Database
import com.developersam.web.auth.FirebaseUser
import com.developersam.web.database.Consumer
import com.developersam.web.database.consumeBy
import com.google.cloud.datastore.StructuredQuery.OrderBy.desc
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.eq

/**
 * The object to obtain a list of [AnalyzedArticle] objects associated
 * with a user.
 */
object AnalyzedArticles {

    /**
     * [get] gives a list of analyzed articles of a user in [printer].
     */
    fun get(user: FirebaseUser, printer: Consumer<List<AnalyzedArticle>>) =
            Database.query(
                    kind = "ChunkReaderText", filter = eq("userEmail", user.email),
                    orderBy = desc("date")
            ) { s ->
                s.map { AnalyzedArticle(entity = it, fullDetail = false) }
                        .toList().consumeBy(consumer = printer)
            }

}
