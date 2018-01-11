package com.developersam.web.util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import javax.annotation.Nullable;

/**
 * A superclass designed to bind closely with DataStore operations.
 * Its subclass must be a logical object related to both a DataStore entity and
 * a Java bean like object.
 */
public abstract class DataStoreObject {
    
    /**
     * Key of parent data store object, can be {@code null}.
     */
    private Key parentKey;
    /**
     * Name of the data store table.
     */
    private final String dataStoreTableName;
    
    /**
     * A data store service that are used by all data store object.
     */
    private static final DatastoreService DATASTORE =
            DatastoreServiceFactory.getDatastoreService();
    
    /**
     * This constructor is used when dataStore object must be initialized to
     * support db operations.
     *
     * @param dataStoreTableName DataStore table name, which specifies for the
     * entire class which kind of object to fetch.
     */
    protected DataStoreObject(String dataStoreTableName) {
        this.dataStoreTableName = dataStoreTableName;
    }
    
    /**
     * Bind a parent key to the object, so that entity generation and query can
     * be associated with its parent.
     * The method does NOT need to be called for all situations.
     *
     * @param parentKey key of parent entity.
     */
    protected void setParentKey(Key parentKey) {
        this.parentKey = parentKey;
    }
    
    /**
     * Obtain the query associated with the entity name (and parent key
     * sometimes).
     *
     * @return query object that can be further modified by filters.
     */
    protected Query getQuery() {
        if (parentKey == null) {
            return new Query(dataStoreTableName);
        } else {
            return new Query(dataStoreTableName).setAncestor(parentKey);
        }
    }
    
    /**
     * Obtain a new entity associated with the entity name  (and parent key
     * sometimes).
     *
     * @return a new entity to be modified and added to database.
     */
    protected Entity getNewEntity() {
        if (parentKey == null) {
            return new Entity(dataStoreTableName);
        } else {
            return new Entity(dataStoreTableName, parentKey);
        }
    }
    
    /**
     * A helper method to obtain an entity by a string form of key.
     *
     * @param key key in string.
     * @return entity with given key.
     */
    @Nullable
    protected static Entity getEntityByKey(String key) {
        return getEntityByKey(KeyFactory.stringToKey(key));
    }
    
    /**
     * A helper method to obtain an entity by key.
     *
     * @param key key of entity.
     * @return entity with given key.
     */
    @Nullable
    protected static Entity getEntityByKey(Key key) {
        try {
            return DATASTORE.get(key);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }
    
    /**
     * A helper method to put an entity into database.
     *
     * @param entity entity to be put into database
     */
    protected static void putIntoDatabase(Entity entity) {
        DATASTORE.put(entity);
    }
    
    /**
     * A helper method to remove an entity from database by its key
     *
     * @param key key of to-be-removed entity
     */
    protected static void removeFromDatabase(Key key) {
        DATASTORE.delete(key);
    }
    
    /**
     * A helper method to obtain prepared query by given a final query
     *
     * @param q final query.
     * @return a prepared query ready to deliver results.
     */
    protected static PreparedQuery getPreparedQuery(Query q) {
        return DATASTORE.prepare(q);
    }
    
    /**
     * A helper method to convert long text to string.
     * It returns null if the text is null.
     *
     * @param text text object from app engine DataStore.
     * @return string form of text.
     */
    private static String textToString(Text text) {
        if (text == null) {
            return null;
        } else {
            return text.getValue();
        }
    }
    
    /**
     * A helper method to convert long text to string.
     * It returns null if the text is null.
     *
     * @param o it must be text object from app engine DataStore.
     * @return string form of text.
     */
    protected static String textToString(Object o) {
        return textToString((Text) o);
    }
    
}