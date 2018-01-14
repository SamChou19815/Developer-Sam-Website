package com.developersam.web.model.chunkreader

import com.google.appengine.api.ThreadManager
import com.google.cloud.language.v1beta2.ClassificationCategory
import com.google.cloud.language.v1beta2.Document
import com.google.cloud.language.v1beta2.Document.Type
import com.google.cloud.language.v1beta2.Entity
import com.google.cloud.language.v1beta2.LanguageServiceClient
import com.google.cloud.language.v1beta2.Sentence
import com.google.cloud.language.v1beta2.Sentiment
import java.util.Arrays
import java.util.concurrent.Callable
import java.util.concurrent.Executors

import com.google.cloud.language.v1beta2.EncodingType.UTF16
import java.util.Collections.emptyList

/**
 * A [NLPAPIAnalyzer] using Google Cloud NLP API directly.
 *
 * It should be constructed from the text needed to analyze.
 */
internal class NLPAPIAnalyzer
private constructor(text: String) {

    /**
     * Sentiment of the entire document.
     */
    @Volatile
    var sentiment: Sentiment? = null
        private set
    /**
     * List of entities extracted from the text.
     */
    @Volatile
    var entities: List<Entity>? = null
        private set
    /**
     * List of sentences extracted from the text.
     */
    @Volatile
    var sentences: List<Sentence>? = null
        private set
    /**
     * List of categories extracted from the text.
     */
    var categories: List<ClassificationCategory>? = null
        private set

    init {
        LanguageServiceClient.create().use { client ->
            val doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Type.PLAIN_TEXT).build()
            val service = Executors.newFixedThreadPool(
                    3, ThreadManager.currentRequestThreadFactory())
            val list = Arrays.asList(Callable<Void> {
                this.sentiment = client.analyzeSentiment(doc).documentSentiment
                null
            }, Callable<Void> {
                this.entities = client
                        .analyzeEntitySentiment(doc, UTF16).entitiesList
                null
            }, Callable<Void> {
                this.sentences = client.analyzeSyntax(doc, UTF16).sentencesList
                null
            })
            service.invokeAll(list)
            // TODO remove these statement when finished debugging.
            println(sentiment)
            println(entities)
            println(sentences)
            // Analyze Categories
            categories = if (entities!!.size > 20) {
                // Google's limitation
                client.classifyText(doc).categoriesList
            } else {
                emptyList<ClassificationCategory>()
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
