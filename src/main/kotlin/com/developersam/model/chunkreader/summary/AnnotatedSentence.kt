package com.developersam.model.chunkreader.summary

import com.developersam.util.datastore.DataStoreObject
import com.developersam.util.datastore.Writable
import com.developersam.util.datastore.dataStore
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.Text

/**
 * An [AnnotatedSentence] is essentially a sentence object marked with a
 * [salience] value to denote its importance.
 * It is used to build a summary generation system that lets the user specify
 * number of sentences to be included in the summary.
 */
class AnnotatedSentence(
        textKey: Key,
        val sentence: String,
        val beginOffset: Int,
        var salience: Double
) : DataStoreObject(kind = "ChunkReaderTextSummary", parent = textKey),
        Writable {

    /**
     * Construct itself from an [Entity] from the database. Used during
     * information retrieval.
     */
    constructor(entity: Entity) : this(entity.parent,
            (entity.getProperty("sentence") as Text).value,
            (entity.getProperty("begin_offset") as Long).toInt(),
            entity.getProperty("salience") as Double)

    override fun writeToDatabase(): Boolean {
        val entity = newEntity
        entity.setProperty("sentence", Text(sentence))
        entity.setProperty("begin_offset", beginOffset)
        entity.setProperty("salience", salience)
        dataStore.put(entity)
        return true
    }

}