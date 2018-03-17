package com.developersam.chunkreader

import com.developersam.util.upsertEntity
import com.google.cloud.datastore.Key

/**
 * [DeferredTypePredictor] is used to help predict the type of the text.
 * Instead of predicting it right now, it stores all the information needed
 * into database, deferring the prediction at query time. This structure
 * allows flexible interpretation of data.
 */
internal object DeferredTypePredictor : ChunkReaderSubProcessor {

    override val name: String = "Deferred Type Predictor"

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        val sentiment = analyzer.sentiment
        upsertEntity(kind = "ChunkReaderText", key = textKey) {
            it.apply {
                set("sentimentScore", sentiment.score.toDouble())
                set("sentimentMagnitude", sentiment.magnitude.toDouble())
                set("tokenCount", analyzer.tokenCount.toLong())
            }
        }
    }

}