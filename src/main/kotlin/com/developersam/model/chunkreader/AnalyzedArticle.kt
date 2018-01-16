package com.developersam.model.chunkreader

import com.developersam.model.chunkreader.category.RetrievedCategories
import com.developersam.model.chunkreader.knowledge.RetrievedKnowledgeMap
import com.developersam.model.chunkreader.summary.RetrievedSummaries
import com.developersam.model.chunkreader.type.TextType
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Text
import com.google.common.base.MoreObjects
import java.util.Date

/**
 * An [AnalyzedArticle] is an article with all the information analyzed and
 * presented in a meaningful way.
 * It is initialized by an `entity` from the database.
 * The user of the class can also specify whether to include all the details
 * by the `fullDetail` flag, which defaults to `false`.
 */
class AnalyzedArticle(entity: Entity, fullDetail: Boolean = false) {

    /**
     * Date of the article submission.
     */
    private val date: Date = entity.getProperty("date") as Date
    /**
     * Title of the article.
     */
    private val title: String = entity.getProperty("title") as String
    /**
     * Number of tokens in the article content.
     */
    private val tokenCount: Long = entity.getProperty("tokenCount") as Long
    /**
     * Content of the article.
     */
    private val content: String?
    /**
     * Type of the recognized text.
     */
    private val textType: TextType?
    /**
     * A map of knowledge type to list of knowledge points
     */
    private val knowledgeMap: RetrievedKnowledgeMap?
    /**
     * A list of summaries of the content.
     */
    private val summaries: RetrievedSummaries?
    /**
     * Categories of the content.
     */
    private val categories: RetrievedCategories?

    init {
        if (fullDetail) {
            content = (entity.getProperty("content") as Text).value
            var sentimentScore =
                    entity.getProperty("sentimentScore") as Double
            var sentimentMagnitude =
                    entity.getProperty("sentimentMagnitude") as Double
            sentimentScore /= tokenCount
            sentimentMagnitude /= tokenCount
            textType = getTextType(
                    score = sentimentScore, magnitude = sentimentMagnitude)
            val textKey: Key = entity.key
            knowledgeMap = RetrievedKnowledgeMap(textKey = textKey)
            summaries = RetrievedSummaries(textKey = textKey)
            categories = RetrievedCategories(textKey = textKey)
        } else {
            content = null
            textType = null
            knowledgeMap = null
            summaries = null
            categories = null
        }
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
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
        private const val scoreThreshold: Double = 0.2
        /**
         * The threshold for a strong sentiment.
         */
        private const val magnitudeThreshold: Double = 3.0

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

    }

}