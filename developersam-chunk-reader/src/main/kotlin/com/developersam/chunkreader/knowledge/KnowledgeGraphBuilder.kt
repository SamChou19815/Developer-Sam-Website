package com.developersam.chunkreader.knowledge

import com.developersam.chunkreader.ChunkReaderSubProcessor
import com.developersam.chunkreader.NLPAPIAnalyzer
import com.google.appengine.api.datastore.Key

/**
 * The object used to help build a knowledge graph.
 * It will extract useful information from the API and store them into the
 * database.
 */
internal object KnowledgeGraphBuilder : ChunkReaderSubProcessor {

    override val name: String = "Knowledge Graph Builder"

    override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
        analyzer.entities.parallelStream()
                .map({ entity -> KnowledgePoint.from(textKey, entity) })
                .distinct()
                .sequential()
                .forEach({ it.writeToDatabase() })
    }

}