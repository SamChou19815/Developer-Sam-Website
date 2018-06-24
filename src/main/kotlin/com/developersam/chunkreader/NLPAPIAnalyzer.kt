package com.developersam.chunkreader

import com.google.cloud.language.v1beta2.AnalyzeSyntaxResponse
import com.google.cloud.language.v1beta2.Document
import com.google.cloud.language.v1beta2.Document.Type
import com.google.cloud.language.v1beta2.EncodingType.UTF16
import com.google.cloud.language.v1beta2.Entity
import com.google.cloud.language.v1beta2.LanguageServiceClient
import com.google.cloud.language.v1beta2.Sentence
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A [NLPAPIAnalyzer] using Google Cloud NLP API directly.
 *
 * It should be constructed from the text needed to analyze.
 */
internal class NLPAPIAnalyzer(text: String) {

    /**
     * [entities] is a list of entities extracted from the text.
     */
    val entities: List<Entity>
    /**
     * [sentences] is a list of sentences extracted from the text.
     */
    val sentences: List<Sentence>
    /**
     * [tokenCount] is number of tokens in the sentence.
     */
    val tokenCount: Int

    /**
     * [service] is the thread poll for this analyzer.
     */
    private val service = Executors.newFixedThreadPool(3)
    /**
     * [latch] is the latch for coordination.
     */
    private val latch = CountDownLatch(2)

    /**
     * [_entities] is the backing field of [entities].
     */
    @Volatile
    private lateinit var _entities: List<Entity>
    /**
     * [_sentences] is the backing field of [sentences].
     */
    @Volatile
    private lateinit var _sentences: List<Sentence>
    /**
     * [tokenCount] is the backing field of [tokenCount].
     */
    @Volatile
    private var _tokenCount: Int = 0

    init {
        val client = LanguageServiceClient.create()
        try {
            val doc: Document = Document.newBuilder()
                    .setContent(text).setType(Type.PLAIN_TEXT).build()
            service.submitWithCountdown {
                _entities = client.analyzeEntitySentiment(doc, UTF16).entitiesList
            }
            service.submitWithCountdown {
                val r: AnalyzeSyntaxResponse = client.analyzeSyntax(doc, UTF16)
                _sentences = r.sentencesList
                _tokenCount = r.tokensCount
            }
            latch.await()
            entities = _entities
            sentences = _sentences
            tokenCount = _tokenCount
        } finally {
            client.close()
            service.shutdown()
        }
    }

    /**
     * [ExecutorService.submitWithCountdown] execute [f] with this [ExecutorService] and make the
     * latch count down by one.
     */
    private inline fun ExecutorService.submitWithCountdown(crossinline f: () -> Unit) {
        submit { f(); latch.countDown() }
    }

}
