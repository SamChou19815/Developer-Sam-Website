package com.developersam.chunkreader.knowledge

import com.developersam.chunkreader.NLPAPIAnalyzer
import com.developersam.main.Database
import com.developersam.web.database.BuildableEntity
import com.developersam.web.database.safeGetString
import com.developersam.web.database.setString
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.hasAncestor
import kotlin.streams.toList
import com.google.cloud.language.v1beta2.Entity as LanguageEntity

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
            Database.createEntityBuilder(kind = kind, parent = textKey)
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
         * Commonly used kind of the entities.
         */
        private const val kind = "ChunkReaderKnowledgeGraph"
    }

    /**
     * [GraphBuilder] is responsible for building a graph.
     */
    object GraphBuilder {

        /**
         * [build] uses the information from [NLPAPIAnalyzer] and [textKey] to build the
         * knowledge graph for the given text.
         */
        @JvmStatic
        fun build(analyzer: NLPAPIAnalyzer, textKey: Key) {
            val s = analyzer.entities
                    .stream()
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
            Database.insertEntities(entities = s)
        }

    }

    /**
     * [RetrievedKnowledgeGraph] used to fetch a list of knowledge points.
     */
    class RetrievedKnowledgeGraph(textKey: Key) {

        /**
         * A list of all knowledge points.
         */
        private val knowledgePoints: List<KnowledgePoint> =
                Database.blockingQuery(
                        kind = kind, filter = hasAncestor(textKey)
                ).map(::KnowledgePoint).sortedByDescending { it.salience }.toList()

        /**
         * Fetch an array of top keywords.
         */
        val asKeywords: List<String> =
                knowledgePoints.stream().limit(3).map { it.name }
                        .toList()

        /**
         * Fetch an organized map from small finite known [KnowledgeType] to a list of
         * [KnowledgePoint] objects associated with the text key given in  constructor.
         */
        val asMap: Map<KnowledgeType, List<KnowledgePoint>> =
                knowledgePoints.asSequence().groupBy { it.type }
                        .onEach { (_, v) ->
                            v.sortedByDescending {
                                it.salience
                            }
                        }

    }

}
