package com.developersam.chunkreader.knowledge

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.Writable
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.Entity as LanguageEntity

/**
 * The [KnowledgePoint] data class represents an entity that the user may have
 * some interest in.
 */
internal class KnowledgePoint private constructor(
        textKey: Key? = null,
        internal val name: String,
        @field:Transient internal val type: KnowledgeType,
        internal val url: String?,
        @field:Transient internal val salience: Double
) : DataStoreObject(kind = "ChunkReaderKnowledgeGraph", parent = textKey),
        Writable {

    /**
     * Construct itself from a database [entity]. Used during information
     * retrieval.
     */
    constructor(entity: Entity) : this(
            name = entity.getProperty("name") as String,
            type = KnowledgeType.valueOf(entity.getProperty("type") as String),
            url = entity.getProperty("url") as String?,
            salience = entity.getProperty("salience") as Double
    )

    override fun writeToDatabase(): Boolean {
        val entity = newEntity
        entity.setProperty("name", name)
        entity.setProperty("type", type.name)
        entity.setProperty("URL", url)
        entity.setProperty("salience", salience)
        dataStore.put(entity)
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is KnowledgePoint) {
            return false
        }
        return name == other.name && type == other.type
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + type.hashCode()
    }

    companion object {

        /**
         * Create a [KnowledgePoint] object from a [textKey] that links to the
         * original text and the language [entity].
         */
        fun fromLanguageEntity(textKey: Key,
                               entity: LanguageEntity): KnowledgePoint {
            val type: KnowledgeType = KnowledgeType.from(entity.type)
            val url: String? = entity.metadataMap["wikipedia_url"]
            return KnowledgePoint(
                    textKey = textKey,
                    name = entity.name,
                    type = type,
                    url = url,
                    salience = entity.salience.toDouble()
            )
        }

    }

}