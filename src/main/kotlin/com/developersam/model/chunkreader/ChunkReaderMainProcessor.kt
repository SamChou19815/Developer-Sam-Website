package com.developersam.model.chunkreader

import com.developersam.model.chunkreader.category.CategoryClassifier
import com.developersam.model.chunkreader.knowledge.KnowledgeGraphBuilder
import com.developersam.model.chunkreader.summary.SentenceSalienceMarker
import com.developersam.model.chunkreader.type.DeferredTypePredictor
import com.developersam.util.datastore.DataStoreObject
import com.developersam.util.datastore.dataStore
import com.google.appengine.api.datastore.Text
import com.google.appengine.api.users.UserServiceFactory
import java.util.Arrays
import java.util.Date

/**
 * [ChunkReaderMainProcessor] calls all the other sub-processors to complete
 * the chunk reader pre-processing.
 */
object ChunkReaderMainProcessor : DataStoreObject("ChunkReaderText") {

    /**
     * Process a given [text] and use [Boolean] return to report whether
     * the processing has succeeded without error.
     */
    fun process(text: String): Boolean {
        val analyzer = NLPAPIAnalyzer.analyze(text) ?: return false
        val processingTaskArray: Array<ChunkReaderSubProcessor> = arrayOf(
                DeferredTypePredictor,
                KnowledgeGraphBuilder,
                SentenceSalienceMarker,
                CategoryClassifier
        )
        val entity = newEntity
        entity.setProperty("userEmail",
                UserServiceFactory.getUserService().currentUser.email)
        entity.setProperty("text", Text(text))
        entity.setProperty("date", Date())
        dataStore.put(entity)
        val textKey = entity.key
        Arrays.stream(processingTaskArray).parallel().forEach {
            it.process(analyzer = analyzer, textKey = textKey)
        }
        return true
    }

}