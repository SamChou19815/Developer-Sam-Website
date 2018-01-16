package com.developersam.model.chunkreader.knowledge

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Key

/**
 * The class used to fetch a list of knowledge points for a common text key.
 */
class RetrievedKnowledgeMap(textKey: Key) :
        DataStoreObject(kind = "ChunkReaderKnowledgeGraph", parent = textKey) {

    /**
     * Fetch an organized map from small finite known [KnowledgeType] to a list
     * of [KnowledgePoint] objects associated with the text key given in
     * constructor.
     */
    val asMap: Map<KnowledgeType, List<KnowledgePoint>>
        get() {
            return dataStore.prepare(query).asIterable().asSequence()
                    .map { entity -> KnowledgePoint(entity = entity) }
                    .groupBy { it.type }
                    .onEach { (_, value) ->
                        value.sortedByDescending { it.salience }
                    }
        }

}