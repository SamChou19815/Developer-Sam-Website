package com.developersam.model.chunkreader

import com.developersam.model.chunkreader.type.TextType
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Text
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
        } else {
            content = null
            textType = null
        }
    }

}

/**
 * The threshold for a biased sentiment.
 */
private val scoreThreshold = 0.2
/**
 * The threshold for a strong sentiment.
 */
private val magnitudeThreshold = 3.0

/**
 * Infer [TextType] from scaled sentiment [score] and sentiment [magnitude].
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