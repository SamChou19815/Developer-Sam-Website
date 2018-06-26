package com.developersam.chunkreader

import com.developersam.chunkreader.Article.Table.content
import com.developersam.chunkreader.Article.Table.title
import com.developersam.typestore.TypedEntity
import com.developersam.typestore.TypedEntityCompanion
import com.developersam.typestore.TypedTable
import com.developersam.typestore.nowInUTC
import com.developersam.typestore.toUTCMillis
import com.developersam.auth.FirebaseUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import kotlin.system.measureTimeMillis
import com.google.cloud.language.v1beta2.Entity as LanguageEntity

/**
 * An [Article] is an article with all the information analyzed and presented in a meaningful way.
 *
 * @constructor It is initialized by an `entity` from the database.
 * The user of the class can also specify whether to include all the details by the `fullDetail`
 * flag, which defaults to `false`.
 */
class Article private constructor(article: ArticleEntity, fullDetail: Boolean) {

    /**
     * [key] is the key string of the article.
     */
    private val key: String = article.key.toUrlSafe()
    /**
     * [time] is the submitted time of the article.
     */
    private val time: Long = article.time
    /**
     * [title] is the title of the article.
     */
    private val title: String = article.title
    /**
     * [tokenCount] is the count of the tokens of the article.
     */
    private val tokenCount: Long = article.tokenCount
    /**
     * [content] is the content of the article.
     */
    private val content: String? = if (fullDetail) article.content else null
    /**
     * [knowledgeMap] is a map of knowledge type to list of knowledge points.
     */
    private val knowledgeMap: Map<Knowledge.Type, List<Knowledge>>? =
            if (fullDetail) Knowledge[article.key] else null
    /**
     * [summaries] is a list of summaries of the content.
     */
    private val summaries: List<String>? =
            if (fullDetail) Summary.getFromKey(textKey = article.key) else null

    /**
     * [Table] is the table definition for the [Article]'s raw text part.
     */
    private object Table : TypedTable<Table>(tableName = "ChunkReaderText") {
        val userId = stringProperty(name = "user_id")
        val time = datetimeProperty(name = "time")
        val title = stringProperty(name = "title")
        val tokenCount = longProperty(name = "token_count")
        val content = longStringProperty(name = "content")
    }

    /**
     * [ArticleEntity] is the entity definition for the [Article]'s raw text part.
     */
    private class ArticleEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val userId: String = Table.userId.delegatedValue
        val time: Long = Table.time.delegatedValue.toUTCMillis()
        val title = Table.title.delegatedValue
        val tokenCount = Table.tokenCount.delegatedValue
        val content = Table.content.delegatedValue
    }

    /**
     * [Db] is the object for database operations.
     */
    private object Db : TypedEntityCompanion<Table, ArticleEntity>(table = Table) {
        override fun create(entity: Entity): ArticleEntity = ArticleEntity(entity = entity)
    }

    /**
     * [Processor] calls all the other sub-processors to complete the chunk reader
     * pre-processing.
     */
    internal object Processor {

        /**
         * [runAnalyzerWithLog] runs the google NLP analyzer on [content] and logs its running time.
         * After the result is fetched, it will returns an optional [NLPAPIAnalyzer] if it
         * completes successfully.
         */
        @JvmStatic
        private fun runAnalyzerWithLog(content: String): NLPAPIAnalyzer? {
            var analyzer: NLPAPIAnalyzer? = null
            val analyzerRunningTime = measureTimeMillis {
                analyzer = try {
                    NLPAPIAnalyzer(text = content)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
            println("NLP API Analyzer finished in ${analyzerRunningTime}ms.")
            return analyzer
        }

        /**
         * Process a given [article] and use [Boolean] for a [user], returning whether the
         * processing has succeeded without error.
         */
        @JvmStatic
        fun process(user: FirebaseUser, article: RawArticle) {
            val (title, content) = article
            val analyzer = runAnalyzerWithLog(content = content) ?: run {
                println("Processing failed!")
                return
            }
            val textKey = Db.insert {
                it[Table.userId] = user.uid
                it[Table.time] = nowInUTC()
                it[Table.title] = title
                it[Table.tokenCount] = analyzer.tokenCount.toLong()
                it[Table.content] = content
            }.key
            measureTimeMillis {
                Knowledge.buildKnowledgeGraph(textKey = textKey, entities = analyzer.entities)
            }.also { println("Knowledge Graph Builder finished in $it ms.") }
            measureTimeMillis {
                Summary.markSentenceSalience(
                        textKey = textKey,
                        sentences = analyzer.sentences.sortedBy { it.text.beginOffset },
                        entities = analyzer.entities
                )
            }.also { println("Sentence Salience Marker finished in $it ms.") }
        }

    }

    companion object {

        /**
         * [userCanAccess] reports whether a [user] can access an article with this [key].
         */
        fun userCanAccess(user: FirebaseUser, key: Key): Boolean =
                Db[key]?.let { it.userId == user.uid } ?: false

        /**
         * [get] returns a list of articles associated with the given [user].
         */
        operator fun get(user: FirebaseUser): List<Article> =
                Db.query {
                    filter = Table.userId eq user.uid
                    order = Table.time.desc()
                }.map { Article(article = it, fullDetail = false) }.toList()

        /**
         * [get] returns a [Article] with full detail from a [key], which may not exist due to a
         * wrong key. It also checks whether the [user] has the permission.
         */
        operator fun get(user: FirebaseUser, key: Key): Article? =
                Db[key]?.takeIf { it.userId == user.uid }?.let { article ->
                    Article(article = article, fullDetail = true)
                }

    }

}
