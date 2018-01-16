package com.developersam.model.chunkreader

import com.developersam.model.chunkreader.category.CategoryClassifier
import com.developersam.model.chunkreader.knowledge.KnowledgeGraphBuilder
import com.developersam.model.chunkreader.summary.SentenceSalienceMarker
import com.developersam.model.chunkreader.type.DeferredTypePredictor
import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Text
import com.google.appengine.api.users.UserServiceFactory
import java.util.Arrays
import java.util.Date
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

/**
 * [ChunkReaderMainProcessor] calls all the other sub-processors to complete
 * the chunk reader pre-processing.
 */
object ChunkReaderMainProcessor : DataStoreObject(kind = "ChunkReaderText") {

    /**
     * Process a given [article] and use [Boolean] return to report whether
     * the processing has succeeded without error.
     */
    fun process(article: RawArticle): Boolean {
        val title = article.title ?: return false
        val content = article.content ?: return false
        val logger = Logger.getGlobal()
        val startTime = System.currentTimeMillis()
        logger.info("Text to be analyzed:\n" + content)
        val analyzer = NLPAPIAnalyzer.analyze(content) ?: return false
        val endTime = System.currentTimeMillis()
        logger.info(
                "NLP API Analyzer finished in ${endTime - startTime}ms.")
        val processingTaskArray: Array<ChunkReaderSubProcessor> = arrayOf(
                DeferredTypePredictor,
                KnowledgeGraphBuilder,
                SentenceSalienceMarker,
                CategoryClassifier
        )
        val entity = newEntity
        entity.setProperty("userEmail",
                UserServiceFactory.getUserService().currentUser.email)
        entity.setProperty("date", Date())
        entity.setProperty("title", title)
        entity.setProperty("content", Text(content))
        dataStore.put(entity)
        val textKey = entity.key
        Arrays.stream(processingTaskArray).parallel().forEach {
            val runningTime = measureTimeMillis {
                it.process(analyzer = analyzer, textKey = textKey)
            }
            logger.info(it.name + " finished in " + runningTime + "ms.")
        }
        return true
    }

}
