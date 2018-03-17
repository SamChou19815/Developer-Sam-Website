@file:JvmName(name = "Knowledge")

package com.developersam.chunkreader.knowledge

import com.developersam.chunkreader.ChunkReaderSubProcessor
import com.developersam.chunkreader.NLPAPIAnalyzer
import com.developersam.util.BuildableEntity
import com.developersam.util.buildNewEntityOf
import com.developersam.util.insertToDatabase
import com.developersam.util.runQueryOf
import com.developersam.util.safeGetString
import com.developersam.util.setString
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor
import com.google.cloud.language.v1beta2.Entity as LanguageEntity

/**
 * Commonly used kind of the entities.
 */
private const val kind = "ChunkReaderKnowledgeGraph"

/**
 * The [KnowledgePoint] data class represents an entity that the user may have
 * some interest in.
 */
internal class KnowledgePoint private constructor(
        @field:Transient private val textKey: Key? = null,
        internal val name: String,
        @field:Transient internal val type: KnowledgeType,
        internal val url: String?,
        @field:Transient internal val salience: Double
) : BuildableEntity {

    /**
     * Construct itself from a database [entity].
     * Used during information retrieval.
     */
    internal constructor(entity: Entity) : this(
            name = entity.getString("name"),
            type = KnowledgeType.valueOf(entity.getString("type")),
            url = entity.safeGetString("url"),
            salience = entity.getDouble("salience")
    )

    override fun toEntityBuilder(): Entity.Builder =
            buildNewEntityOf(kind = kind, parent = textKey)
                    .set("name", name)
                    .set("type", type.name)
                    .setString("URL", url)
                    .set("salience", salience)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is KnowledgePoint) {
            return false
        }
        return name == other.name && type == other.type
    }

    override fun hashCode(): Int = name.hashCode() * 31 + type.hashCode()

    companion object {

        /**
         * [graphBuilder] is used to help build a knowledge graph.
         * It will extract useful information from the API and store them into
         * the database.
         */
        val graphBuilder = object : ChunkReaderSubProcessor {

            override val name: String = "Knowledge Graph Builder"

            override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) =
                    analyzer.entities
                            .parallelStream()
                            .map {
                                KnowledgePoint(
                                        textKey = textKey,
                                        name = it.name,
                                        type = KnowledgeType.from(it.type),
                                        url = it.metadataMap["wikipedia_url"],
                                        salience = it.salience.toDouble()
                                )
                            }
                            .distinct()
                            .insertToDatabase()
        }

    }

}

/**
 * [RetrievedKnowledgeGraph] used to fetch a list of knowledge points for a
 * common text key.
 */
internal class RetrievedKnowledgeGraph(textKey: Key) {

    /**
     * A list of all knowledge points.
     */
    private val knowledgePoints: List<KnowledgePoint> =
            runQueryOf(kind = kind, filter = hasAncestor(textKey))
                    .map(::KnowledgePoint)
                    .sortedByDescending { it.salience }
                    .toList()

    /**
     * Fetch an array of top keywords.
     */
    val asKeywords: Array<String>
        get() = knowledgePoints.stream()
                .limit(3)
                .map { it.name }
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