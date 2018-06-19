package com.developersam.chunkreader.summary

import com.developersam.chunkreader.NLPAPIAnalyzer
import com.developersam.main.Database
import com.google.cloud.datastore.Key
import com.google.cloud.language.v1beta2.Sentence
import com.google.cloud.language.v1beta2.TextSpan
import com.google.common.collect.Sets
import java.util.Arrays

/**
 * [SentenceSalienceMarker] is used to mark each sentence with a salience value by Text Rank
 * algorithm.
 * It will use the processed data from the API to help further analyze the importance of each
 * sentence.
 */
internal object SentenceSalienceMarker {

    /**
     * Text Rank algorithm constant.
     */
    private const val D = 0.85
    /**
     * The threshold for convergence.
     */
    private const val CONVERGENCE_THRESHOLD = 1e-3
    /**
     * A list of [AnnotatedSentence] to be written into database.
     */
    private lateinit var annotatedSentences: MutableList<AnnotatedSentence>
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
     * Obtain a random sentence id from the [annotatedSentences].
     */
    private val randomSentenceID: Int get() = (Math.random() * annotatedSentences.size).toInt()

    /**
     * A helper method to find the index of [beginOffset] from an array of begin offsets that
     * represents ranges of sentence.
     */
    @JvmStatic
    private fun IntArray.getBeginOffsetIndex(beginOffset: Int): Int {
        val rawIndex = Arrays.binarySearch(this, beginOffset)
        if (rawIndex >= 0) {
            return rawIndex
        }
        // a + ~a + 1 = 0 ==> ~a = -1 - a => a = ~(-1 - a)
        val insertionPoint = rawIndex.inv()
        // Obtain the inversion point from API
        return insertionPoint - 1
    }

    /**
     * Calculate the similarity of two sentences, specified by their IDs ([sentence1], [sentence2])
     * as a [Double].
     */
    @JvmStatic
    private fun computeSimilarity(sentence1: Int, sentence2: Int): Double {
        val numCommon = Sets.intersection(
                keywordsArray[sentence1], keywordsArray[sentence2]).size
        val sentence1Length = annotatedSentences[sentence1].sentence.length
        val sentence2Length = annotatedSentences[sentence2].sentence.length
        if (sentence1Length == 0 || sentence2Length == 0) {
            throw RuntimeException("Zero length sentence!")
        }
        if (sentence1Length == 1) {
            throw RuntimeException("Abnormal sentence: "
                    + annotatedSentences[sentence1].sentence)
        }
        if (sentence2Length == 1) {
            throw RuntimeException("Abnormal sentence: "
                    + annotatedSentences[sentence2].sentence)
        }
        return numCommon.toDouble() / (Math.log(sentence1Length.toDouble() +
                Math.log(sentence2Length.toDouble())))
    }

    /**
     * Build the given sentences and entities into a data structure like a graph that allows the
     * random walk along the sentences graph from the incoming [analyzer] and a [textKey].
     */
    @JvmStatic
    private fun initSentenceGraph(analyzer: NLPAPIAnalyzer, textKey: Key) {
        /*
         * Sort the sentence by its beginning index, so that the list
         * represents the entire paragraph.
         */
        val sentences = analyzer.sentences.sortedBy { it.text.beginOffset }
        val num = sentences.size
        // Late-init instance variables
        annotatedSentences = ArrayList(num)
        sentenceRanges = IntArray(num)
        keywordsArray = Array(size = num) { HashSet<String>() }
        similarityMatrix = Array(size = num) {
            DoubleArray(num)
        }
        for (i in 0 until num) {
            val sentence: Sentence = sentences[i]
            val text: TextSpan = sentence.text
            val beginOffset: Int = text.beginOffset
            // Build a list of annotated sentences.
            annotatedSentences.add(AnnotatedSentence(
                    textKey = textKey,
                    sentence = text.content,
                    beginOffset = beginOffset.toLong(),
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
        for (i in 0 until (num - 1)) {
            for (j in (i + 1) until num) {
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
     * Compare the current salience value in [annotatedSentences] to a previous value to check
     * whether they are convergent.
     */
    @JvmStatic
    private fun convergent(previousSalienceArray: DoubleArray): Boolean {
        for (i in previousSalienceArray.indices) {
            val diff = previousSalienceArray[i] - annotatedSentences[i].salience
            if (Math.abs(diff) > CONVERGENCE_THRESHOLD) {
                return false
            }
        }
        return true
    }

    /**
     * Use the Text Rank algorithm to randomly visit the sentence graph until the their annotated
     * salience value has converge.
     */
    @JvmStatic
    private fun randomVisit() {
        val num = annotatedSentences.size
        if (num == 1) {
            // No need to run the program, always the selected sentence.
            return
        }
        var startSentenceIndex = randomSentenceID
        var start = annotatedSentences[startSentenceIndex]
        var counter = 0
        val previousSalienceArray = DoubleArray(size = num) {
            Short.MIN_VALUE.toDouble()
        }
        while (true) {
            if (counter % num == 0) {
                // Check convergence every `num` times
                if (convergent(previousSalienceArray = previousSalienceArray)) {
                    return
                }
                // Record for future use?
                for (i in 0 until num) {
                    previousSalienceArray[i] = annotatedSentences[i].salience
                }
            }
            /**
             * The following block of code computes a new salience according to
             * Text Rank algorithm.
             */
            var sum = 0.0
            for (i in 0 until num) {
                if (i == startSentenceIndex) {
                    continue
                }
                val anotherSentence = annotatedSentences[i]
                val numerator = similarityMatrix[i][startSentenceIndex]
                var denominator = 0.0
                for (j in 0 until num) {
                    if (i == j) {
                        continue
                    }
                    denominator += similarityMatrix[i][j]
                }
                if (denominator < 0) {
                    throw RuntimeException("Bad addition!")
                }
                if (denominator > 1e-6) {
                    sum += numerator / denominator * anotherSentence.salience
                }
            }
            val newSalience = (1 - D) + D * sum
            // Update with a better salience value
            start.salience = newSalience
            counter++
            // Randomly choose another sentence to continue the loop.
            while (true) {
                val i = randomSentenceID
                if (i != startSentenceIndex) {
                    startSentenceIndex = i
                    start = annotatedSentences[i]
                    break
                }
            }
        }
    }

    /**
     * [mark] uses the information from [NLPAPIAnalyzer] and [textKey] to mark salience values for
     * sentences.
     */
    @JvmStatic
    fun mark(analyzer: NLPAPIAnalyzer, textKey: Key) {
        initSentenceGraph(analyzer = analyzer, textKey = textKey)
        randomVisit()
        Database.insertEntities(entities = annotatedSentences.stream())
    }

}
