package com.developersam.chunkreader

import com.developersam.auth.FirebaseUser
import com.developersam.database.runQueryOf
import com.google.cloud.datastore.StructuredQuery.OrderBy.desc
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.eq

/**
 * The object to obtain a list of [AnalyzedArticle] objects associated
 * with a user.
 */
object AnalyzedArticles {

    /**
     * [get] returns a list of analyzed articles of a user.
     */
    operator fun get(user: FirebaseUser): List<AnalyzedArticle> =
            runQueryOf(
                    kind = "ChunkReaderText",
                    filter = eq("userEmail", user.email),
                    orderBy = desc("date")
            ).map { AnalyzedArticle(entity = it, fullDetail = false) }.toList()

}