package com.developersam.model.chunkreader.category

import com.developersam.model.chunkreader.ChunkReaderProcessor
import com.developersam.model.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key

/**
 * The object used to find the categories of a chunk of text.
 */
object CategoryClassifier : ChunkReaderProcessor {

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        TODO("not implemented")
    }

}