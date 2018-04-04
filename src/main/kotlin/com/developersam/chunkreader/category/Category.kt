@file:JvmName(name = "Categories")

package com.developersam.chunkreader.category

import com.developersam.chunkreader.ChunkReaderSubProcessor
import com.developersam.chunkreader.NLPAPIAnalyzer
import com.developersam.database.BuildableEntity
import com.developersam.database.DatastoreClient
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.StructuredQuery.OrderBy.desc
import com.google.cloud.datastore.StructuredQuery.PropertyFilter

/**
 * Commonly used kind of the entities.
 */
private const val kind = "ChunkReaderContentCategory"

/**
 * [Category] represents a category of the content with all the statistical
 * data.
 */
internal class Category private constructor(
        @field:Transient private val textKey: Key? = null,
        internal val name: String,
        private val confidence: Double
) : BuildableEntity {

    override fun toEntityBuilder(): Entity.Builder =
            DatastoreClient.createEntityBuilder(kind = kind, parent = textKey)
                    .set("name", name)
                    .set("confidence", confidence)

    companion object {
        /**
         * [retrieve] fetches a list of categories in string form associated
         * with the given [textKey].
         */
        fun retrieve(textKey: Key): List<String> =
                DatastoreClient.blockingQuery(
                        kind = kind,
                        filter = PropertyFilter.hasAncestor(textKey),
                        orderBy = desc("confidence"),
                        limit = 3
                ).map {
                    val fullName = it.getString("name")
                    fullName.substring(
                            startIndex = fullName.lastIndexOf(char = '/') + 1)
                }.toList()

        /**
         * [classifier] is used to find the categories of a chunk of
         * text.
         */
        val classifier = object : ChunkReaderSubProcessor {
            override val name: String = "Category Classifier"

            override fun process(analyzer: NLPAPIAnalyzer, textKey: Key) {
                val s = analyzer.categories
                        .parallelStream()
                        .map {
                            Category(
                                    textKey = textKey,
                                    name = it.name,
                                    confidence = it.confidence.toDouble()
                            )
                        }
                DatastoreClient.insertEntities(entities = s)
            }
        }
    }

}