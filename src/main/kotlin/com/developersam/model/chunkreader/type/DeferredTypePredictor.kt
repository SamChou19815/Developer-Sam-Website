package com.developersam.model.chunkreader.type

import com.developersam.model.chunkreader.ChunkReaderProcessor
import com.developersam.model.chunkreader.NLPAPIAnalyzer
import com.developersam.util.datastore.dataStore
import com.developersam.util.datastore.getEntityByKey
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.Sentiment
import java.lang.RuntimeException

/**
 * The object used to help predict the type of the text.
 * Instead of predicting it right now, it stores all the information needed
 * into database, deferring the prediction at query time. This structure
 * allows flexible interpretation of data.
 */
object DeferredTypePredictor : ChunkReaderProcessor {

    private fun getTextType(sentiment: Sentiment): TextType {
        val scoreThreshold = 0.2
        val magnitudeThreshold = 3.0
        val score = sentiment.score
        val magnitude = sentiment.magnitude
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

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        val sentiment = analyzer.sentiment
        val entity = dataStore.getEntityByKey(textKey)
                ?: throw RuntimeException("Text Entity is not in the database!")
        entity.setProperty("sentiment_score", sentiment.score)
        entity.setProperty("sentiment_magnitude", sentiment.score)
        entity.setProperty("token_count", analyzer.tokenCount)
        dataStore.put(entity)
    }

}