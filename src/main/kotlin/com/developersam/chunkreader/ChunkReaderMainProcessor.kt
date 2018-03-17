package com.developersam.chunkreader

import com.developersam.auth.FirebaseUser
import com.developersam.chunkreader.category.Category
import com.developersam.chunkreader.knowledge.KnowledgePoint
import com.developersam.chunkreader.summary.SentenceSalienceMarker
import com.developersam.util.buildAndInsertEntity
import com.developersam.util.buildStringValue
import com.google.cloud.Timestamp
import java.util.concurrent.Executors
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

/**
 * [ChunkReaderMainProcessor] calls all the other sub-processors to complete
 * the chunk reader pre-processing.
 */
object ChunkReaderMainProcessor {

    /**
     * Process a given [article] and use [Boolean] for a [user], returning
     * whether the processing has succeeded without error.
     */
    fun process(user: FirebaseUser, article: RawArticle): Boolean {
        val title = article.title ?: return false
        val content = article.content ?: return false
        val startTime = System.currentTimeMillis()
        val analyzer = NLPAPIAnalyzer.analyze(content) ?: return false
        val endTime = System.currentTimeMillis()
        val logger = Logger.getGlobal()
        logger.info("NLP API Analyzer finished in ${endTime - startTime}ms.")
        val processingTaskArray: Array<ChunkReaderSubProcessor> = arrayOf(
                KnowledgePoint.graphBuilder,
                SentenceSalienceMarker,
                Category.classifier
        )
        val textKey = buildAndInsertEntity(kind = "ChunkReaderText") {
            it.apply {
                set("userEmail", user.email)
                set("date", Timestamp.now())
                set("title", title)
                set("content", buildStringValue(string = content))
                set("tokenCount", analyzer.tokenCount.toLong())
            }
        }
        val runningT = measureTimeMillis {
            DeferredTypePredictor.process(analyzer, textKey)
        }
        logger.info("Deferred Type Predictor finished in $runningT ms.")
        val service = Executors.newFixedThreadPool(processingTaskArray.size)
        processingTaskArray.forEach {
            service.submit {
                val runningTime = measureTimeMillis {
                    it.process(analyzer = analyzer, textKey = textKey)
                }
                logger.info("${it.name} finished in $runningTime ms.")
            }
        }
        return true
    }

}
