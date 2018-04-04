package com.developersam.web.database

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreException
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.Filter
import com.google.cloud.datastore.StructuredQuery.OrderBy
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import java.util.stream.Stream

/**
 * [DatastoreClient] is a client for the database operations on Google Cloud
 * Datastore. It wraps the low level APIs with Vert.x-like API to support
 * non-blocking DB operations.
 *
 * @constructor it needs a [vertx] object to run async operations.
 */
open class DatastoreClient(private val vertx: Vertx) {

    /**
     * The globally used datastore object.
     */
    private val datastore: Datastore =
            DatastoreOptions.getDefaultInstance().service

    /**
     * The dummy handler to handle `null` handlers.
     */
    private val dummyHandler: Handler<AsyncResult<Any>> = Handler {}

    /**
     * [getHandler] creates a handler of Vert.x from the given consumer [c].
     * If [c] is empty, then the handler created is a dummy handler which
     * directly ignores the result.
     */
    @Suppress(names = ["UNCHECKED_CAST"])
    private fun <R> getHandler(c: Consumer<R>?): Handler<AsyncResult<R>> {
        if (c == null) {
            return dummyHandler as Handler<AsyncResult<R>>
        }
        return Handler { res ->
            if (res.succeeded()) {
                res.result().consumeBy(consumer = c)
            } else if (res.failed()) {
                throw res.cause()
            }
        }
    }

    /**
     * [runBlocking] runs some blocking code specified by [body] and passes the
     * result in [consumer].
     */
    private inline fun <R> runBlocking(
            noinline consumer: Consumer<R>? = null,
            crossinline body: Producer<R>) {
        vertx.executeBlocking(Handler {
            it.complete(body.invoke())
        }, false, getHandler(consumer))
    }

    // Part 1: Entities

    /**
     * [createEntityBuilder] creates a partially built entity with a specified
     * entity [kind] and an optional [parent] if it has a parent.
     */
    fun createEntityBuilder(kind: String, parent: Key? = null): Entity.Builder {
        val incompleteKey = if (parent == null) {
            datastore.newKeyFactory()
                    .setKind(kind)
                    .newKey()
        } else {
            datastore.newKeyFactory()
                    .addAncestor(PathElement.of(parent.kind, parent.id))
                    .setKind(kind)
                    .newKey()
        }
        val key = datastore.allocateId(incompleteKey)
        return Entity.newBuilder(key)
    }

    /**
     * [get] returns an immutable entity given from a key string in
     * URL safe encoding. The entity may or may not exist.
     */
    operator fun get(key: String): Entity? {
        return try {
            datastore[Key.fromUrlSafe(key)]
        } catch (e: DatastoreException) {
            null
        }
    }

    // Part 2: Queries

    /**
     * [blockingQuery] creates a query with a specified [kind].
     * It also accepts optional [filter] and an [orderBy] function to filter and
     * sort things.
     *
     * @return a sequence of entities.
     */
    fun blockingQuery(kind: String,
                      filter: Filter? = null,
                      orderBy: OrderBy? = null,
                      limit: Int? = null): Sequence<Entity> {
        val builder = Query.newEntityQueryBuilder().setKind(kind)
        filter?.run { builder.setFilter(this) }
        orderBy?.run { builder.setOrderBy(this) }
        limit?.run { builder.setLimit(this) }
        return datastore.run(builder.build()).asSequence()
    }

    /**
     * [query] creates a query with a specified [kind].
     * It also accepts optional [filter] and an [orderBy] function to filter and
     * sort things.
     *
     * The found entities are returned via the [consumer].
     */
    fun query(kind: String, filter: Filter? = null,
              orderBy: OrderBy? = null, limit: Int? = null,
              consumer: Consumer<Sequence<Entity>>) {
        runBlocking(consumer = consumer) {
            blockingQuery(kind, filter, orderBy, limit)
        }
    }

    // Part 3: Modifications

    /**
     * [buildAndInsertEntity] builds an entity with specified [kind] with an
     * optional [parent] by a given [constructor]. The resultant key of the
     * built entity is passed by the [consumer].
     */
    fun buildAndInsertEntity(kind: String, parent: Key? = null,
                             constructor: (Entity.Builder) -> Unit,
                             consumer: Consumer<Key>? = null) {
        val newEntityBuilder = createEntityBuilder(kind = kind, parent = parent)
        constructor(newEntityBuilder)
        val entity = newEntityBuilder.build()
        runBlocking(consumer = consumer) { datastore.add(entity).key }
    }

    /**
     * [insertEntities] inserts a stream of buildable entities into the
     * database and returns a list of built entities via [consumer].
     */
    fun insertEntities(entities: Stream<out BuildableEntity>,
                       consumer: Consumer<List<Entity>>? = null) {
        val array = entities.map(BuildableEntity::toEntity)
                .toArray { size ->
                    arrayOfNulls<Entity>(size)
                }
        runBlocking(consumer = consumer) { datastore.add(*array) }
    }

    /**
     * [update] performs a simple update operation on an [entity] by its
     * [builder]. The resultant entity is passed by the [consumer].
     * This function is suitable for quickly updating a single value.
     */
    fun update(entity: Entity, updater: (Entity.Builder) -> Unit,
               consumer: Consumer<Entity>? = null) {
        val builder = Entity.newBuilder(entity)
        updater(builder)
        runBlocking(consumer = consumer) { datastore.put(builder.build()) }
    }

    /**
     * [upsertEntity] upserts an entity of a given [kind], which may have a
     * [parent]. The entity may or may not exist, depending on whether [key]
     * has a concrete value or `null`. Then the entity will be rebuilt with the
     * specified [constructor] function.
     *
     * This function also optionally accepts a [validator], which should check
     * the existing content in an [Entity] to see whether the upsert operation
     * should proceed. It is usually used as a user permission checker.
     * This function processes the entity by transaction if needed, which is
     * safe by default. All upsert operation that involves updating the value
     * should call this function.
     *
     * Note that this is an asynchronous operation. When it returns, the
     * operation is not necessarily finished.
     */
    fun upsertEntity(kind: String, key: Key?, parent: Key? = null,
                     validator: ((Entity) -> Boolean)? = null,
                     constructor: (Entity.Builder) -> Unit) {
        if (key == null) {
            buildAndInsertEntity(
                    kind = kind, parent = parent, constructor = constructor
            )
        }
        runBlocking {
            val txn = datastore.newTransaction()
            try {
                val entity = txn[key]
                val validated = validator == null || validator(entity)
                if (validated) {
                    val builder = Entity.newBuilder(entity)
                    constructor(builder)
                    txn.put(builder.build())
                }
                txn.commit()
            } finally {
                if (txn.isActive) {
                    txn.rollback()
                }
            }
            key
        }
    }

    /**
     * [updateEntities] update entities of given [keys]. The entities will be
     * rebuilt with the specified [constructor] function.
     * This function processes the entity by transaction if needed, which is
     * safe by default. All batch update operation that involves updating the
     * value should call this function.
     *
     * It returns a list of built entities via [handler].
     */
    fun updateEntities(vararg keys: Key,
                       constructor: (Entity.Builder) -> Unit,
                       consumer: Consumer<List<Entity>>? = null) {
        val txn = datastore.newTransaction()
        try {
            runBlocking(consumer = consumer) {
                val updatedEntities = txn.fetch(*keys).parallelStream()
                        .map {
                            val builder = Entity.newBuilder(it)
                            constructor(builder)
                            builder.build()
                        }
                        .toArray { size -> arrayOfNulls<Entity>(size) }
                val entities = txn.put(*updatedEntities)
                txn.commit()
                entities
            }
        } finally {
            if (txn.isActive) {
                txn.rollback()
            }
        }
    }

    // Part 4: Delete Entities

    /**
     * [deleteEntity] removes an entity with given [keyString] from the
     * database.
     * It also accepts an optional [validator] to check whether the removal
     * request was legal. It returns the validation result.
     *
     * Note that this is an asynchronous operation. When it returns, the
     * operation is not necessarily finished.
     */
    fun deleteEntity(keyString: String,
                     validator: ((String) -> Boolean)? = null): Boolean {
        val validated = validator?.invoke(keyString) ?: true
        if (validated) {
            runBlocking { datastore.delete(Key.fromUrlSafe(keyString)) }
            return true
        }
        return false
    }
}
