@file:JvmName("DatastoreUtil")

package com.developersam.util

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreException
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery.CompositeFilter.and
import com.google.cloud.datastore.StructuredQuery.Filter
import com.google.cloud.datastore.StructuredQuery.OrderBy
import java.util.Date
import java.util.stream.Stream

/**
 * These functions are responsible for packing the API from Google Cloud Client
 * Library and delivering them in a more convenient way.
 */

/**
 * The globally used data store object.
 */
private val datastore: Datastore = DatastoreOptions.getDefaultInstance().service

// Part 1: Entities

/**
 * [buildStringValue] creates a [StringValue] from a long [String].
 */
fun buildStringValue(string: String): StringValue =
        StringValue.newBuilder(string).setExcludeFromIndexes(true).build()

/**
 * [Entity.safeGetLong] obtains a long property of an entity in a null safe way.
 */
fun Entity.safeGetLong(name: String): Long? =
        takeIf { contains(name) && !isNull(name) }?.getLong(name)

/**
 * [Entity.safeGetString] obtains a string property of an entity in a null
 * safe way.
 */
fun Entity.safeGetString(name: String): String? =
        takeIf { contains(name) && !isNull(name) }?.getString(name)

/**
 * [buildNewEntityOf] creates a partially built entity with a specified
 * entity [kind] and an optional [parent] if it has a parent.
 */
fun buildNewEntityOf(kind: String, parent: Key? = null): Entity.Builder {
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
 * [getEntityByKey] returns an immutable entity given from a key string in
 * URL safe encoding. The entity may or may not exist.
 */
fun getEntityByKey(key: String): Entity? {
    return try {
        datastore[Key.fromUrlSafe(key)]
    } catch (e: DatastoreException) {
        null
    }
}

/**
 * [builder] is the builder of an entity with pre-populated data from itself.
 */
private val Entity.builder: Entity.Builder
    get() = Entity.newBuilder(this)

/**
 * [Timestamp.toDate] converts a Google Cloud [Timestamp] object to a [Date]
 * object for convenience use.
 */
fun Timestamp.toDate(): Date = this.toSqlTimestamp()

// Part 2: Queries

/**
 * [and] acts as a syntax sugar to write more readable filters.
 */
infix fun Filter.and(filter: Filter): Filter = and(this, filter)

/**
 * [runQueryOf] creates a partially built query with a specified [kind] and
 * returns a [Sequence] of entities.
 * It also accepts optional [filter] and an [orderBy] function to filter and
 * sort things.
 */
fun runQueryOf(kind: String, filter: Filter? = null,
               orderBy: OrderBy? = null, limit: Int? = null): Sequence<Entity> {
    val builder = Query.newEntityQueryBuilder().setKind(kind)
    filter?.run { builder.setFilter(this) }
    orderBy?.run { builder.setOrderBy(this) }
    limit?.run { builder.setLimit(this) }
    val query = builder.build()
    return datastore.run(query).asSequence()
}

// Part 3: Modifications

/**
 * [Entity.Builder.set] only sets the long value if the given long is not null.
 */
fun Entity.Builder.set(name: String, value: Long?): Entity.Builder =
        value?.let { this.set(name, it) } ?: this

/**
 * [Entity.Builder.setString] only sets the string value if the given String is
 * not null. Otherwise, the value is set to be null.
 */
fun Entity.Builder.setString(name: String, value: String?): Entity.Builder =
        if (value == null) {
            setNull(name)
        } else {
            set(name, value)
        }

/**
 * The function type to update an [Entity.Builder], which is part of the entity
 * updating process.
 */
private typealias EntityConstructor = (Entity.Builder) -> Unit

/**
 * [Entity.update] performs a simple update operation on itself
 * This function is suitable for quickly updating a single value.
 */
fun Entity.update(construct: EntityConstructor) {
    val builder = this.builder
    construct(builder)
    datastore.put(builder.build())
}

/**
 * [buildAndInsertEntity] builds an entity with specified [kind] with an
 * optional [parent] by a given [construct], and returns the [Key] of the built
 * entity.
 */
fun buildAndInsertEntity(kind: String, parent: Key? = null,
                         construct: EntityConstructor): Key {
    val newEntityBuilder = buildNewEntityOf(kind = kind, parent = parent)
    construct(newEntityBuilder)
    val entity = newEntityBuilder.build()
    datastore.add(entity)
    return entity.key
}

/**
 * [insertToDatabase] inserts a stream of buildable entities into the database.
 */
fun Stream<out BuildableEntity>.insertToDatabase() {
    val array = this.map(BuildableEntity::toEntity).toArray { size ->
        arrayOfNulls<Entity>(size)
    }
    datastore.add(*array)
}

/**
 * [upsertEntity] upserts an entity of a given [kind], which may have a
 * [parent]. The entity may or may not exist, depending on whether [key] has a
 * concrete value or null. Then the entity will be rebuilt with the specified
 * [construct] function.
 * This function also optionally accepts a [validator], which should check the
 * existing content in an [Entity] to see whether the upsert operation should
 * proceed. It is usually used as a user permission checker.
 * This function processes the entity by transaction if needed, which is safe by
 * default. All upsert operation that involves updating the value should call
 * this function.
 */
fun upsertEntity(kind: String, key: Key?, parent: Key? = null,
                 validator: ((Entity) -> Boolean)? = null,
                 construct: EntityConstructor): Boolean {
    if (key == null) {
        buildAndInsertEntity(
                kind = kind,
                parent = parent,
                construct = construct
        )
        return true
    }
    val txn = datastore.newTransaction()
    try {
        val entity = txn[key]
        val validated = validator == null || validator(entity)
        if (!validated) {
            txn.commit()
            return false
        }
        val builder = entity.builder
        construct(builder)
        txn.put(builder.build())
        txn.commit()
        return true
    } finally {
        if (txn.isActive) {
            txn.rollback()
        }
    }
}

/**
 * [updateEntities] update entities of a given [kind]. The entities will be
 * rebuilt with the specified [construct] function.
 * This function processes the entity by transaction if needed, which is safe by
 * default. All batch update operation that involves updating the value should
 * call this function.
 */
fun updateEntities(vararg keys: Key, construct: EntityConstructor) {
    val txn = datastore.newTransaction()
    try {
        val updatedEntities = txn.fetch(*keys).parallelStream()
                .map {
                    val builder = it.builder
                    construct(builder)
                    builder.build()
                }
                .toArray { size -> arrayOfNulls<Entity>(size) }
        txn.put(*updatedEntities)
        txn.commit()
    } finally {
        if (txn.isActive) {
            txn.rollback()
        }
    }
}

// Part 4: Delete Entities

/**
 * [deleteEntity] removes an entity with given [keyString] from the database.
 * It also accepts an optional [validator] to check whether the removal request
 * was legal.
 */
fun deleteEntity(keyString: String,
                 validator: ((String) -> Boolean)? = null): Boolean {
    val validated = validator?.invoke(keyString) ?: true
    if (validated) {
        datastore.delete(Key.fromUrlSafe(keyString))
        return true
    }
    return false
}