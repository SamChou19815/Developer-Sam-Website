package com.developersam.model.chunkreader

import com.developersam.model.chunkreader.category.CategoryClassifier
import com.developersam.model.chunkreader.knowledge.KnowledgeGraphBuilder
import com.developersam.model.chunkreader.summary.SentenceSalienceMarker
import com.developersam.model.chunkreader.type.DeferredTypePredictor
import com.google.appengine.api.datastore.Key
import java.util.Arrays

/**
 * The object used as the main processor that calls all the other sub-processors
 * to complete the chunk reader pre-processing.
 */
object ChunkReaderMainProcessor : ChunkReaderProcessor {

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        val processingTaskArray: Array<ChunkReaderProcessor> = arrayOf(
                DeferredTypePredictor,
                KnowledgeGraphBuilder,
                SentenceSalienceMarker,
                CategoryClassifier
        )
        Arrays.stream(processingTaskArray).parallel().forEach {
            it.process(analyzer = analyzer, textKey = textKey)
        }
    }

}