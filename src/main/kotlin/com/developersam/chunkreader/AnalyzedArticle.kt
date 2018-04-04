package com.developersam.chunkreader

import com.developersam.chunkreader.category.Category
import com.developersam.chunkreader.knowledge.KnowledgePoint
import com.developersam.chunkreader.knowledge.KnowledgeType
import com.developersam.chunkreader.knowledge.RetrievedKnowledgeGraph
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.main.Database
import com.developersam.web.database.toDate
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StringValue
import com.google.common.base.MoreObjects
import java.util.Date

/**
 * An [AnalyzedArticle] is an article with all the information analyzed and
 * presented in a meaningful way.
 *
 * @constructor It is initialized by an `entity` from the database.
 * The user of the class can also specify whether to include all the details
 * by the `fullDetail` flag, which defaults to `false`.
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
    private val keyString: String = textKey.toUrlSafe()
    /**
     * Date of the article submission.
     */
    private val date: Date = entity.getTimestamp("date").toDate()
    /**
     * Title of the article.
     */
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
     * Type of the recognized text.
     */
    private val textType: String?
    /**
     * An array of article keywords.
     */
    private val keywords: Array<String>?
    /**
     * A map of knowledge type to list of knowledge points
     */
    private val knowledgeMap: Map<KnowledgeType, List<KnowledgePoint>>?
    /**
     * A list of summaries of the content.
     */
    private val summaries: List<String>?
    /**
     * Categories of the content.
     */
    private val categories: List<String>?

    init {
        if (fullDetail) {
            // Basic Info (Type)
            content = entity.getValue<StringValue>("content").get()
            val sentimentScore = entity.getDouble("sentimentScore")
            val sentimentMagnitude = entity.getDouble("sentimentMagnitude")
            val score = sentimentScore / Math.log(tokenCount.toDouble())
            val magnitude = sentimentMagnitude / Math.log(tokenCount.toDouble())
            textType = getTextType(score, magnitude).toString()
            // Advanced Info (Knowledge, Summary, Category)
            val retrievedKnowledgeGraph = RetrievedKnowledgeGraph(textKey)
            keywords = retrievedKnowledgeGraph.asKeywords
            knowledgeMap = retrievedKnowledgeGraph.asMap
            summaries = RetrievedSummaries(textKey).asList
            categories = Category.retrieve(textKey)
        } else {
            content = null
            textType = null
            keywords = null
            knowledgeMap = null
            summaries = null
            categories = null
        }
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("keyString", keyString)
                .add("date", date)
                .add("title", title)
                .add("content", content)
                .add("knowledgeMap", knowledgeMap)
                .add("summaries", summaries)
                .add("categories", categories)
                .toString()
    }

    companion object {

        /**
         * The threshold for a biased sentiment.
         */
        private const val scoreThreshold: Double = 0.05
        /**
         * The threshold for a strong sentiment.
         */
        private const val magnitudeThreshold: Double = 0.84

        /**
         * Infer [TextType] from scaled sentiment [score] and
         * sentiment [magnitude].
         */
        private fun getTextType(score: Double, magnitude: Double): TextType {
            return when {
                score < -scoreThreshold -> if (magnitude < magnitudeThreshold) {
                    TextType.SLIGHT_OPPOSITION
                } else {
                    TextType.STRONG_OPPOSITION
                }
                score < scoreThreshold -> if (magnitude < magnitudeThreshold) {
                    TextType.CONCEPT
                } else {
                    TextType.MIXED
                }
                else -> if (magnitude < magnitudeThreshold) {
                    TextType.SLIGHT_SUPPORT
                } else {
                    TextType.STRONG_SUPPORT
                }
            }
        }

        /**
         * Create a [AnalyzedArticle] with full detail from a [keyString],
         * which may not be created due to a wrong key.
         */
        fun fromKey(keyString: String?): AnalyzedArticle? =
                keyString?.let { k ->
                    Database[k]?.let {
                        AnalyzedArticle(entity = it, fullDetail = true)
                    }
                }
    }

}