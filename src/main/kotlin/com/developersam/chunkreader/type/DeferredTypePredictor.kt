package com.developersam.chunkreader.type

import com.developersam.chunkreader.ChunkReaderSubProcessor
import com.developersam.chunkreader.NLPAPIAnalyzer
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.datastore.getEntityByKey
import com.google.appengine.api.datastore.Key
import java.lang.RuntimeException

/**
 * The object used to help predict the type of the text.
 * Instead of predicting it right now, it stores all the information needed
 * into database, deferring the prediction at query time. This structure
 * allows flexible interpretation of data.
 */
internal object DeferredTypePredictor : ChunkReaderSubProcessor {

    override val name: String = "Deferred Type Predictor"

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        val sentiment = analyzer.sentiment
        val entity = dataStore.getEntityByKey(textKey)
                ?: throw RuntimeException("Text Entity is not in the database!")
        entity.setProperty("sentimentScore", sentiment.score.toDouble())
        entity.setProperty("sentimentMagnitude", sentiment.magnitude.toDouble())
        entity.setProperty("tokenCount", analyzer.tokenCount.toLong())
        dataStore.put(entity)
    }

}