package com.developersam.model.chunkreader.summary

import com.developersam.model.chunkreader.ChunkReaderProcessor
import com.developersam.model.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.Sentence
import com.google.cloud.language.v1beta2.TextSpan
import com.google.common.collect.Sets
import java.util.Arrays

/**
 * The abstract class used to mark each sentence with a salience value by Text
 * Rank algorithm.
 * It will use the processed data from the API to help further analyze
 * the importance of each sentence.
 */
abstract class SentenceSalienceMarker : ChunkReaderProcessor {

    /**
     * A list of [AnnotatedSentence] to be written into database.
     */
    private lateinit var annotatedSentences: ArrayList<AnnotatedSentence>
    /**
     * An array that represents the ranges of each sentence.
     */
    private lateinit var sentenceRanges: IntArray
    /**
     * An array of sets of keywords of each sentence.
     */
    private lateinit var keywordsArray: Array<MutableSet<String>>
    /**
     * The similarity matrix between sentences.
     */
    private lateinit var similarityMatrix: Array<DoubleArray>

    /**
     * Obtain a random [AnnotatedSentence] from the [annotatedSentences].
     */
    private val randomAnnotatedSentence: AnnotatedSentence
        get() {
            val randomIndex = (Math.random() * annotatedSentences.size).toInt()
            return annotatedSentences[randomIndex]
        }

    /**
     * Calculate the similarity of two sentences, specified by their IDs
     * ([sentence1], [sentence2]) as a [Double].
     */
    private fun computeSimilarity(sentence1: Int, sentence2: Int): Double {
        val numCommon = Sets.intersection(
                keywordsArray[sentence1], keywordsArray[sentence2]).size
        val sentence1Length = annotatedSentences[sentence1].sentence.length
        val sentence2Length = annotatedSentences[sentence2].sentence.length
        return numCommon.toDouble() / (Math.log(sentence1Length.toDouble() +
                Math.log(sentence2Length.toDouble())))
    }

    /**
     * Build the given sentences and entities into a data structure like a graph
     * that allows the random walk along the sentences graph.
     */
    private fun initSentenceGraph(analyzer: NLPAPIAnalyzer, textKey: Key) {
        /*
         * Sort the sentence by its beginning index, so that the list
         * represents the entire paragraph.
         */
        val sentences = analyzer.sentences.sortedBy { it.text.beginOffset }
        val numberOfSentences = sentences.size
        // Late-init instance variables
        annotatedSentences = ArrayList(numberOfSentences)
        sentenceRanges = IntArray(numberOfSentences)
        keywordsArray = Array(size = numberOfSentences) { HashSet<String>() }
        similarityMatrix = Array(size = numberOfSentences) {
            DoubleArray(numberOfSentences)
        }
        for (i in 0 until numberOfSentences) {
            val sentence: Sentence = sentences[i]
            val text: TextSpan = sentence.text
            val beginOffset: Int = text.beginOffset
            // Build a list of annotated sentences.
            annotatedSentences.add(AnnotatedSentence(
                    textKey = textKey,
                    sentence = text.content,
                    beginOffset = beginOffset,
                    salience = Math.random() // Random init salience
            ))
            // Build an array of sentence ranges.
            sentenceRanges[i] = beginOffset
        }
        // Build the keywords array to find keywords mapped to each sentence.
        for (entity in analyzer.entities) {
            for (entityMention in entity.mentionsList) {
                val entityText: TextSpan = entityMention.text
                val sentenceID: Int = sentenceRanges.getBeginOffsetIndex(
                        entityText.beginOffset)
                keywordsArray[sentenceID].add(entityText.content)
            }
        }
        // Build the similarity matrix
        for (i in 0 until (numberOfSentences - 1)) {
            for (j in (i + 1) until numberOfSentences) {
                if (i == j) {
                    continue
                }
                val similarity = computeSimilarity(sentence1 = i, sentence2 = j)
                similarityMatrix[i][j] = similarity
                similarityMatrix[j][i] = similarity
            }
        }
    }

    /**
     * I don't know what it's doing, for now.
     */
    private fun convergent(previousResult: DoubleArray): Boolean {
        val threshold = 1e-3
        for (i in previousResult.indices) {
            val diff = previousResult[i] - annotatedSentences[i].salience
            if (Math.abs(diff) > threshold) {
                return false
            }
        }
        return true
    }

    /**
     * Use the Text Rank algorithm to randomly visit the sentence graph until
     * the their annotated salience value has converge.
     */
    private fun randomVisit() {
        TODO("Not implemented!")
    }

    override final fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        initSentenceGraph(analyzer = analyzer, textKey = textKey)
        randomVisit()
        annotatedSentences.parallelStream().forEach { it.writeToDatabase() }
    }

}

/**
 * A helper method to find the index of [beginOffset] from an array of begin
 * offsets that represents ranges of sentence.
 */
private fun IntArray.getBeginOffsetIndex(beginOffset: Int): Int {
    val rawIndex = Arrays.binarySearch(this, beginOffset)
    if (rawIndex >= 0) {
        return rawIndex
    }
    // a + ~a + 1 = 0 ==> ~a = -1 - a => a = ~(-1 - a)
    val insertionPoint = rawIndex.inv() // Obtain the inversion point from API
    return insertionPoint - 1
}