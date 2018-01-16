package com.developersam.model.chunkreader.category

import com.developersam.model.chunkreader.ChunkReaderSubProcessor
import com.developersam.model.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key

/**
 * The object used to find the categories of a chunk of text.
 */
object CategoryClassifier : ChunkReaderSubProcessor {

    override val name: String = "Category Classifier"

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        analyzer.categories.forEach { classificationCategory ->
            Category.from(textKey = textKey, category = classificationCategory)
                    .writeToDatabase()
        }
    }

}