package com.developersam.chunkreader.category

import com.developersam.chunkreader.ChunkReaderSubProcessor
import com.developersam.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key

/**
 * The object used to find the categories of a chunk of text.
 */
internal object CategoryClassifier : ChunkReaderSubProcessor {

    override val name: String = "Category Classifier"

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        analyzer.categories.forEach { classificationCategory ->
            Category.fromAnalyzedCategory(
                    textKey = textKey, category = classificationCategory)
                    .writeToDatabase()
        }
    }

}