package com.developersam.web.util

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.EntityNotFoundException
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Query

/**
 * Obtain the global data store service.
 */
val dataStore: DatastoreService = DatastoreServiceFactory.getDatastoreService()

/**
 * Obtain an [Entity] by [key] from the [dataStore].
 */
fun DatastoreService.getEntityByKey(key: Key): Entity? {
    return try {
        dataStore.get(key)
    } catch (e: EntityNotFoundException) {
        null
    }
}

/**
 * Obtain an [Entity] by [key] from the [dataStore].
 */
fun DatastoreService.getEntityByKey(key: String): Entity?
        = getEntityByKey(KeyFactory.stringToKey(key))

/**
 * A superclass designed to bind closely with DataStore operations.
 * Its subclass must be a logical object related to both a DataStore entity and
 * a Java bean like object.
 * The abstract class is initialized by a [dataStoreTableName] and an optional
 * [parentKey] used to fetch its parent.
 */
abstract class DataStoreObject
protected constructor(private val dataStoreTableName: String,
                      private val parentKey: Key? = null) {

    /**
     * Obtain the query associated with the entity name (and parent key
     * sometimes).
     */
    protected val query: Query
        get() = if (parentKey == null) {
            Query(dataStoreTableName)
        } else {
            Query(dataStoreTableName).setAncestor(parentKey)
        }

    /**
     * Obtain a new [Entity] associated with the entity name (and parent key
     * if it exists).
     */
    protected val newEntity: Entity
        get() = if (parentKey == null) {
            Entity(dataStoreTableName)
        } else {
            Entity(dataStoreTableName, parentKey)
        }

}