package com.developersam.chunkreader.summary

import com.developersam.database.BuildableEntity
import com.developersam.database.buildNewEntityOf
import com.developersam.database.buildStringValue
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StringValue

/**
 * [AnnotatedSentence] is essentially a sentence object marked with a
 * [salience] value to denote its importance.
 * It is used to build a summary generation system that lets the user specify
 * number of sentences to be included in the summary.
 */
internal class AnnotatedSentence(
        @field:Transient private val textKey: Key,
        internal val sentence: String,
        @field:Transient internal val beginOffset: Long,
        @field:Transient internal var salience: Double
) : BuildableEntity {

    /**
     * Construct itself from an [Entity] from the database. Used during
     * information retrieval.
     */
    internal constructor(entity: Entity) : this(
            textKey = entity.key.parent,
            sentence = entity.getValue<StringValue>("sentence").get(),
            beginOffset = entity.getLong("begin_offset"),
            salience = entity.getDouble("salience")
    )

    override fun toEntityBuilder(): com.google.cloud.datastore.Entity.Builder =
            buildNewEntityOf(kind = "ChunkReaderTextSummary", parent = textKey)
                    .set("sentence", buildStringValue(string = sentence))
                    .set("begin_offset", beginOffset)
                    .set("salience", salience)

}