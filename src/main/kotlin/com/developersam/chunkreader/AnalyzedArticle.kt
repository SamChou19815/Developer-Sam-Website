package com.developersam.chunkreader

import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.main.Database
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StringValue
import java.util.Date

/**
 * An [AnalyzedArticle] is an article with all the information analyzed and presented in a
 * meaningful way.
 *
 * @constructor It is initialized by an `entity` from the database.
 * The user of the class can also specify whether to include all the details by the `fullDetail`
 * flag, which defaults to `false`.
 */
class AnalyzedArticle(entity: Entity, fullDetail: Boolean = false) {

    /**
     * Key of the text.
     */
    @field:Transient
    private val textKey: Key = entity.key
    /**
     * Key string of the article for later client info retrieval.
     */
    @Suppress(names = ["unused"])
    private val keyString: String = textKey.toUrlSafe()
    /**
     * Date of the article submission.
     */
    @Suppress(names = ["unused"])
    private val date: Date = entity.getTimestamp("date").toDate()
    /**
     * Title of the article.
     */
    @Suppress(names = ["unused"])
    private val title: String = entity.getString("title")
    /**
     * Number of tokens in the article content.
     */
    private val tokenCount: Long = entity.getLong("tokenCount")
    /**
     * Content of the article.
     */
    private val content: String?
    /**
     * An array of article keywords.
     */
    private val keywords: List<String>?
    /**
     * A map of knowledge type to list of knowledge points
     */
    private val knowledgeMap: Map<Knowledge.Type, List<Knowledge>>?
    /**
     * A list of summaries of the content.
     */
    private val summaries: List<String>?

    init {
        if (fullDetail) {
            // Basic Info (Type)
            content = entity.getValue<StringValue>("content").get()
            // Advanced Info (Knowledge, Summary, Category)
            val retrievedKnowledgeGraph = Knowledge.RetrievedKnowledgeGraph(textKey)
            keywords = retrievedKnowledgeGraph.asKeywords
            knowledgeMap = retrievedKnowledgeGraph.asMap
            summaries = RetrievedSummaries(textKey).asList
        } else {
            content = null
            keywords = null
            knowledgeMap = null
            summaries = null
        }
    }

    companion object {

        /**
         * Create a [AnalyzedArticle] with full detail from a [keyString], which may not be
         * created due to a wrong key.
         */
        fun fromKey(keyString: String?): AnalyzedArticle? =
                keyString?.let { k ->
                    Database[k]?.let {
                        AnalyzedArticle(entity = it, fullDetail = true)
                    }
                }
    }

}