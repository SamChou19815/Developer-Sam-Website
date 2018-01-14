package com.developersam.web.model.chunkreader

import com.google.appengine.api.ThreadManager
import com.google.cloud.language.v1beta2.AnalyzeSyntaxResponse
import com.google.cloud.language.v1beta2.ClassificationCategory
import com.google.cloud.language.v1beta2.Document
import com.google.cloud.language.v1beta2.Document.Type
import com.google.cloud.language.v1beta2.Entity
import com.google.cloud.language.v1beta2.LanguageServiceClient
import com.google.cloud.language.v1beta2.Sentence
import com.google.cloud.language.v1beta2.Sentiment
import java.util.concurrent.Executors

import com.google.cloud.language.v1beta2.EncodingType.UTF16
import java.util.Collections.emptyList
import java.util.concurrent.CountDownLatch

/**
 * A [NLPAPIAnalyzer] using Google Cloud NLP API directly.
 *
 * It should be constructed from the text needed to analyze.
 */
class NLPAPIAnalyzer private constructor(text: String) {

    /**
     * Sentiment of the entire document.
     */
    @Volatile
    lateinit internal var sentiment: Sentiment
        private set
    /**
     * List of entities extracted from the text.
     */
    @Volatile
    lateinit internal var entities: List<Entity>
        private set
    /**
     * List of sentences extracted from the text.
     */
    @Volatile
    lateinit internal var sentences: List<Sentence>
        private set
    /**
     * List of categories extracted from the text.
     */
    lateinit internal var categories: List<ClassificationCategory>
        private set
    /**
     * Number of tokens in the sentence.
     */
    @Volatile
    internal var tokenCount: Int = 0
        private set

    init {
        LanguageServiceClient.create().use { client ->
            val doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Type.PLAIN_TEXT).build()
            val service = Executors.newFixedThreadPool(
                    3, ThreadManager.currentRequestThreadFactory())
            val latch = CountDownLatch(3)
            service.submit({
                sentiment = client.analyzeSentiment(doc).documentSentiment
                latch.countDown()
            })
            service.submit({
                entities =
                        client.analyzeEntitySentiment(doc, UTF16).entitiesList
                latch.countDown()
            })
            service.submit({
                val r: AnalyzeSyntaxResponse = client.analyzeSyntax(doc, UTF16)
                tokenCount = r.tokensCount
                sentences = r.sentencesList
                latch.countDown()
            })
            latch.await()
            // Analyze Categories
            categories = if (tokenCount > 25) {
                // Google's limitation
                client.classifyText(doc).categoriesList
            } else {
                emptyList()
            }
        }
    }

    companion object Factory {
        /**
         * Obtain an analyzer that has already analyzed the text.
         *
         * @param text text to be analyzed.
         * @return the analysis result, or `null` if the API request failed.
         */
        fun analyze(text: String): NLPAPIAnalyzer? {
            return try {
                NLPAPIAnalyzer(text)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}
