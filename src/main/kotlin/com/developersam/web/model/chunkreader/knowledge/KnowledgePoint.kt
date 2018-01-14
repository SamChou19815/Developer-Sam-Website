package com.developersam.web.model.chunkreader.knowledge

import com.developersam.web.util.DataStoreObject
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.Entity

/**
 * The [KnowledgePoint] data class represents an entity that the user may have
 * some interest in.
 */
class KnowledgePoint
private constructor(
        textKey: Key,
        val name: String,
        val type: KnowledgeType,
        val url: String,
        val salience: Float
) : DataStoreObject("ChunkReaderKnowledgeGraph", parent = textKey) {

    companion object Factory {
        /**
         * Create a [KnowledgePoint] object from a [textKey] that links to the
         * original text and the [entity]. The new object may or may not be
         * created (due to missing information).
         */
        internal fun from(textKey: Key, entity: Entity): KnowledgePoint? {
            val type: KnowledgeType = KnowledgeType.from(entity.type)
                    ?: return null
            val url: String = entity.metadataMap["wikipedia_url"] ?: return null
            return KnowledgePoint(textKey = textKey, name = entity.name,
                    type = type, url = url, salience = entity.salience)
        }
    }

}
