package com.developersam.chunkreader

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
 * The [Knowledge] data class represents an entity that the user may have some interest in.
 */
internal class Knowledge private constructor(
        @field:Transient private val textKey: Key? = null,
        internal val name: String,
        @field:Transient internal val type: Type,
        internal val url: String?,
        @field:Transient internal val salience: Double
) : BuildableEntity {

    /**
     * Construct itself from a database [entity].
     * Used during information retrieval.
     */
    internal constructor(entity: Entity) : this(
            name = entity.getString("name"),
            type = Type.valueOf(entity.getString("type")),
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
        if (other !is Knowledge) {
            return false
        }
        return name == other.name && type == other.type
    }

    override fun hashCode(): Int = name.hashCode() * 31 + type.hashCode()

    /**
     * A collection of all known knowledge entity types.
     */
    enum class Type { PERSON, LOCATION, ORGANIZATION, EVENT, WORK_OF_ART, CONSUMER_GOOD, UNKNOWN }

    companion object {

        /**
         * Commonly used kind of the entities.
         */
        private const val kind = "ChunkReaderKnowledgeGraph"

        /**
         * Convert an [entityType] from GCP to a [Type] in the system.
         */
        private fun convertKnowledgeType(entityType: LanguageEntity.Type): Type =
                when (entityType) {
                    LanguageEntity.Type.PERSON -> Type.PERSON
                    LanguageEntity.Type.LOCATION -> Type.LOCATION
                    LanguageEntity.Type.ORGANIZATION -> Type.ORGANIZATION
                    LanguageEntity.Type.EVENT -> Type.EVENT
                    LanguageEntity.Type.WORK_OF_ART -> Type.WORK_OF_ART
                    LanguageEntity.Type.CONSUMER_GOOD -> Type.CONSUMER_GOOD
                    LanguageEntity.Type.OTHER,
                    LanguageEntity.Type.UNKNOWN,
                    LanguageEntity.Type.UNRECOGNIZED -> Type.UNKNOWN
                }

    }

    /**
     * [GraphBuilder] is responsible for building a graph.
     */
    internal object GraphBuilder {

        /**
         * [build] uses the information from [NLPAPIAnalyzer] and [textKey] to build the
         * knowledge graph for the given text.
         */
        @JvmStatic
        fun build(analyzer: NLPAPIAnalyzer, textKey: Key) {
            val s = analyzer.entities
                    .stream()
                    .map {
                        Knowledge(
                                textKey = textKey,
                                name = it.name,
                                type = convertKnowledgeType(entityType = it.type),
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
        private val knowledgePoints: List<Knowledge> =
                Database.blockingQuery(
                        kind = kind, filter = hasAncestor(textKey)
                ).map(::Knowledge).sortedByDescending { it.salience }.toList()

        /**
         * Fetch an array of top keywords.
         */
        val asKeywords: List<String> =
                knowledgePoints.stream().limit(3).map { it.name }
                        .toList()

        /**
         * Fetch an organized map from small finite known [KnowledgeType] to a list of
         * [Knowledge] objects associated with the text key given in  constructor.
         */
        val asMap: Map<Type, List<Knowledge>> =
                knowledgePoints.asSequence().groupBy { it.type }
                        .onEach { (_, v) ->
                            v.sortedByDescending {
                                it.salience
                            }
                        }

    }

}
