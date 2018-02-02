package com.developersam.chunkreader

import com.developersam.chunkreader.category.RetrievedCategories
import com.developersam.chunkreader.knowledge.KnowledgePoint
import com.developersam.chunkreader.knowledge.KnowledgeType
import com.developersam.chunkreader.knowledge.RetrievedKnowledgeGraph
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.type.TextType
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.datastore.getEntityByKey
import com.developersam.webcore.exception.AccessDeniedException
import com.developersam.webcore.service.GoogleUserService
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Text
import com.google.common.base.MoreObjects
import java.util.Date
import java.util.logging.Logger

/**
 * An [AnalyzedArticle] is an article with all the information analyzed and
 * presented in a meaningful way.
 */
class AnalyzedArticle {

    /**
     * Key string of the article for later client info retrieval.
     */
    private val keyString: String
    /**
     * Date of the article submission.
     */
    private val date: Date
    /**
     * Title of the article.
     */
    private val title: String
    /**
     * Number of tokens in the article content.
     */
    private val tokenCount: Long
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

    /**
     * It is initialized by an [entity] from the database.
     * The user of the class can also specify whether to include all the details
     * by the [fullDetail] flag, which defaults to `false`.
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(entity: Entity, fullDetail: Boolean = false) {
        val textKey: Key = entity.key
        keyString = KeyFactory.keyToString(textKey)
        val email = GoogleUserService.currentUser?.email
                ?: throw AccessDeniedException()
        val userEmail = entity.getProperty("userEmail") as String
        if (email != userEmail) {
            throw AccessDeniedException()
        }
        date = entity.getProperty("date") as Date
        title = entity.getProperty("title") as String
        tokenCount = entity.getProperty("tokenCount") as Long
        if (!fullDetail) {
            content = null
            textType = null
            keywords = null
            knowledgeMap = null
            summaries = null
            categories = null
            return
        }
        content = (entity.getProperty("content") as Text).value
        val sentimentScore = entity.getProperty("sentimentScore") as Double
        val sentimentMagnitude =
                entity.getProperty("sentimentMagnitude") as Double
        val score = sentimentScore / Math.log(tokenCount.toDouble())
        val magnitude = sentimentMagnitude / Math.log(tokenCount.toDouble())
        textType = getTextType(score = score, magnitude = magnitude).toString()
        val retrievedKnowledgeGraph = RetrievedKnowledgeGraph(textKey = textKey)
        keywords = retrievedKnowledgeGraph.asKeywords
        knowledgeMap = retrievedKnowledgeGraph.asMap
        summaries = RetrievedSummaries(textKey = textKey).asList
        categories = RetrievedCategories(textKey = textKey).asList
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
            val type = when {
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
            val msg = "Type: $type. Score: $score. Magnitude: $magnitude"
            Logger.getGlobal().info(msg)
            return type
        }

        /**
         * Create a [AnalyzedArticle] with full detail from a [keyString],
         * which may not be created due to a wrong key.
         */
        fun fromKey(keyString: String): AnalyzedArticle? {
            val entity = dataStore.getEntityByKey(key = keyString)
                    ?: return null
            return AnalyzedArticle(entity = entity, fullDetail = true)
        }
    }

}