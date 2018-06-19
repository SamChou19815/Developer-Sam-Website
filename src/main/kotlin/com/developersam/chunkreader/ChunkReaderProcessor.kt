package com.developersam.chunkreader

import com.developersam.chunkreader.knowledge.KnowledgePoint
import com.developersam.chunkreader.summary.SentenceSalienceMarker
import com.developersam.main.Database
import com.developersam.web.auth.FirebaseUser
import com.developersam.web.database.buildStringValue
import com.google.cloud.Timestamp
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

/**
 * [ChunkReaderProcessor] calls all the other sub-processors to complete the chunk reader
 * pre-processing.
 */
object ChunkReaderProcessor {

    /**
     * [runAnalyzerWithLog] runs the google NLP analyzer on [content] and logs its running time.
     * After the result is fetched, it will returns an optional [NLPAPIAnalyzer] if it completes
     * successfully.
     */
    @JvmStatic
    private fun runAnalyzerWithLog(content: String): NLPAPIAnalyzer? {
        var analyzer: NLPAPIAnalyzer? = null
        val analyzerRunningTime = measureTimeMillis {
            analyzer = try {
                NLPAPIAnalyzer(text = content)
            } catch (e: Exception) {
                return null
            }
        }
        println("NLP API Analyzer finished in ${analyzerRunningTime}ms.")
        return analyzer
    }

    /**
     * Process a given [article] and use [Boolean] for a [user], returning whether the processing
     * has succeeded without error.
     */
    @JvmStatic
    fun process(user: FirebaseUser, article: RawArticle): Boolean {
        val (title, content) = article.takeIf { it.isValid } ?: return false
        val analyzer = runAnalyzerWithLog(content = content) ?: return false
        Database.buildAndInsertEntity(
                kind = "ChunkReaderText",
                constructor = {
                    it.apply {
                        set("userEmail", user.email)
                        set("date", Timestamp.now())
                        set("title", title)
                        set("content", buildStringValue(content))
                        set("sentimentScore", analyzer.sentiment.score.toDouble())
                        set("sentimentMagnitude", analyzer.sentiment.magnitude.toDouble())
                        set("tokenCount", analyzer.tokenCount.toLong())
                    }
                }) { textKey ->
            val services = Executors.newFixedThreadPool(3)
            services.submit {
                val runningTime = measureTimeMillis {
                    KnowledgePoint.GraphBuilder.build(analyzer = analyzer, textKey = textKey)
                }
                println("Knowledge Graph Builder finished in $runningTime ms.")
            }
            services.submit {
                val runningTime = measureTimeMillis {
                    SentenceSalienceMarker.mark(analyzer = analyzer, textKey = textKey)
                }
                println("Sentence Salience Marker finished in $runningTime ms.")
            }
        }
        return true
    }

}
