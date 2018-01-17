package com.developersam.chunkreader.knowledge

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Key

/**
 * The class used to fetch a list of knowledge points for a common text key.
 */
internal class RetrievedKnowledgeGraph(textKey: Key) :
        DataStoreObject(kind = "ChunkReaderKnowledgeGraph", parent = textKey) {

    /**
     * A list of all knowledge points.
     */
    private val knowledgePoints: List<KnowledgePoint>;

    init {
        knowledgePoints = dataStore.prepare(query).asIterable().asSequence()
                .map { entity -> KnowledgePoint(entity = entity) }
                .sortedByDescending { it.salience }
                .toList()
    }

    /**
     * Fetch an array of top keywords.
     */
    val asKeywords: Array<String>
        get() = knowledgePoints.stream()
                .limit(3)
                .map({ it.name })
                .toArray { size -> arrayOfNulls<String>(size) }

    /**
     * Fetch an organized map from small finite known [KnowledgeType] to a list
     * of [KnowledgePoint] objects associated with the text key given in
     * constructor.
     */
    val asMap: Map<KnowledgeType, List<KnowledgePoint>>
        get() = knowledgePoints.asSequence()
                .groupBy { it.type }
                .onEach { (_, value) ->
                    value.sortedByDescending { it.salience }
                }

}