package com.developersam.model.chunkreader.category

import com.developersam.webcore.datastore.DataStoreObject
import com.developersam.webcore.datastore.Writable
import com.developersam.webcore.datastore.dataStore
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.cloud.language.v1beta2.ClassificationCategory

/**
 * The data class [Category] represents a category of the content.
 */
class Category private constructor(
        textKey: Key? = null,
        internal val name: String,
        private val confidence: Double
) : DataStoreObject(kind = "ChunkReaderContentCategory", parent = textKey),
        Writable {

    override fun writeToDatabase(): Boolean {
        val entity = newEntity
        entity.setProperty("name", name)
        entity.setProperty("confidence", confidence)
        dataStore.put(entity)
        return true
    }

    companion object Factory {
        /**
         * Create a [Category] object from a [textKey] that links to the
         * original text and the [ClassificationCategory].
         */
        fun from(textKey: Key, category: ClassificationCategory): Category {
            return Category(textKey = textKey, name = category.name,
                    confidence = category.confidence.toDouble())
        }

        /**
         * Create a [Category] object from an [entity] from the datastore.
         */
        fun from(entity: Entity): Category {
            val fullName = entity.getProperty("name") as String
            val simpleName = fullName.substring(
                    startIndex = fullName.lastIndexOf(char = '/') + 1)
            val confidence = entity.getProperty("confidence") as Double
            return Category(name = simpleName, confidence = confidence)
        }
    }

}