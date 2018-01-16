package com.developersam.model.chunkreader.knowledge

import com.developersam.model.chunkreader.ChunkReaderSubProcessor
import com.developersam.model.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key

/**
 * The object used to help build a knowledge graph.
 * It will extract useful information from the API and store them into the
 * database.
 */
object KnowledgeGraphBuilder : ChunkReaderSubProcessor {

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        analyzer.entities.parallelStream()
                .map({ entity -> KnowledgePoint.from(textKey, entity) })
                .distinct()
                .forEach({ it.writeToDatabase() })
    }

}