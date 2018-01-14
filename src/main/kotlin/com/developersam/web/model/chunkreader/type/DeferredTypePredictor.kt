package com.developersam.web.model.chunkreader.type

import com.developersam.web.model.chunkreader.ChunkReaderProcessor
import com.developersam.web.model.chunkreader.NLPAPIAnalyzer
import com.developersam.web.util.dataStore
import com.developersam.web.util.getEntityByKey
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.Sentiment
import java.lang.RuntimeException

/**
 * The object used to help predict the type of the text.
 * Instead of predicting it right now, it stores all the information needed
 * into database, deferring the prediction at query time. This structure
 * allows flexible interpretation of data.
 */
internal object DeferredTypePredictor : ChunkReaderProcessor {

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
        entity.setProperty("entity_size", analyzer.entitySize)
        dataStore.put(entity)
    }

}