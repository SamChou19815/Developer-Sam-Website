package com.developersam.chunkreader

import com.developersam.chunkreader.category.CategoryClassifier
import com.developersam.chunkreader.knowledge.KnowledgeGraphBuilder
import com.developersam.chunkreader.summary.SentenceSalienceMarker
import com.developersam.chunkreader.type.DeferredTypePredictor
import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.developersam.webcore.exception.AccessDeniedException
import com.developersam.webcore.service.GoogleUserService
import com.google.appengine.api.ThreadManager
import com.google.appengine.api.datastore.Text
import com.google.appengine.api.users.UserServiceFactory
import java.util.Date
import java.util.concurrent.CountDownLatch
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
        val analyzer = NLPAPIAnalyzer.analyze(content)
                ?: return false
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
        val userEmail = GoogleUserService.currentUser?.email
                ?: throw AccessDeniedException()
        entity.setProperty("userEmail", userEmail)
        entity.setProperty("date", Date())
        entity.setProperty("title", title)
        entity.setProperty("content", Text(content))
        entity.setProperty("tokenCount", analyzer.tokenCount.toLong())
        dataStore.put(entity)
        val textKey = entity.key
        val latch = CountDownLatch(processingTaskArray.size)
        for (processor in processingTaskArray) {
            ThreadManager.createThreadForCurrentRequest {
                val runningTime = measureTimeMillis {
                    processor.process(analyzer = analyzer, textKey = textKey)
                }
                logger.info(processor.name + " finished in "
                        + runningTime + "ms.")
                latch.countDown()
            }.run()
        }
        return try {
            latch.await()
            true
        } catch (e: InterruptedException) {
            logger.throwing("ChunkReaderMainProcessor",
                    "process", e)
            false
        }
    }

}
