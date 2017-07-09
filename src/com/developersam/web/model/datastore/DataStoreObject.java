package com.developersam.web.model.datastore;

import com.google.appengine.api.datastore.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A superclass designed to bind closely with DataStore operations.
 * Its subclass must be a logical object related to both a DataStore entity and a Java bean like object.
 */
public class DataStoreObject {

    private Key parentKey;
    private String dataStoreTableName;
    private DatastoreService dataStore;

    /**
     * The default constructor is used when dataStore does not need to be initialized.
     */
    protected DataStoreObject(){}

    /**
     * This constructor is used when dataStore object must be initialized to support db operations.
     * @param dataStoreTableName DataStore table name
     */
    protected DataStoreObject(String dataStoreTableName) {
        this.dataStoreTableName = dataStoreTableName;
        dataStore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Bind a parent key to the object, so that entity generation and query can be associated with its parent.
     * The method does NOT need to be called for all situations.
     * @param parentKey key of parent entity
     */
    protected void setParentKey(Key parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * Obtain the query associated with the entity name (and parent key sometimes)
     * @return query object that can be further modified by filters
     */
    protected Query getQuery() {
        if (parentKey == null) {
            return new Query(dataStoreTableName);
        }else {
            return new Query(dataStoreTableName).setAncestor(parentKey);
        }
    }

    /**
     * Obtain a new entity associated with the entity name  (and parent key sometimes)
     * @return a new entity to be modified and added to database
     */
    protected Entity getNewEntity() {
        if (parentKey == null) {
            return new Entity(dataStoreTableName);
        }else {
            return new Entity(dataStoreTableName, parentKey);
        }
    }

    /**
     * A helper method to obtain an entity by key
     * @param key key of entity
     * @return entity with given key
     */
    protected Entity getEntityByKey(Key key) {
        try {
            return dataStore.get(key);
        }catch (EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * A helper method to put an entity into database.
     * @param entity entity to be put into database
     */
    protected void putIntoDatabase(Entity entity) {
        dataStore.put(entity);
    }

    /**
     * A helper method to remove an entity from database by its key
     * @param key key of to-be-removed entity
     */
    protected void removeFromDatabase(Key key) {
        dataStore.delete(key);
    }

    /**
     * A helper method to obtain prepared query by given a final query
     * @param q final query
     * @return a prepared query ready to deliver results
     */
    protected PreparedQuery getPreparedQuery(Query q) {
        return dataStore.prepare(q);
    }

    /**
     * A helper method to convert long text to string.
     * It returns null if the text is null.
     * @param text text object from app engine DataStore
     * @return string form of text
     */
    private static String textToString(Text text) {
        if (text == null) {
            return null;
        }else {
            return text.getValue();
        }
    }

    /**
     * A helper method to convert long text to string.
     * It returns null if the text is null.
     * @param o it must be text object from app engine DataStore
     * @return string form of text
     */
    protected static String textToString(Object o) {
        return textToString((Text) o);
    }

    /**
     * format a date object to yyyy-MM-dd hh:mm in EST
     * @param date date object
     * @return a string representation of time in EST (US New York)
     */
    protected static String dateFormatter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return formatter.format(calendar.getTime());
    }

}