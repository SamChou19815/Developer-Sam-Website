package com.developersam.web.model.scheduler;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SchedulerItem extends DataStoreObject {

    private Key key;

    private String description;
    private Date deadline;

    /**
     * Used when fetching a new entity.
     * @param projectEntity a project entity
     */
    SchedulerItem(Entity projectEntity) {
        super("SchedulerItem");
        this.key = projectEntity.getKey();
        this.description = (String) projectEntity.getProperty("description");
        this.deadline = (Date) projectEntity.getProperty("deadline");
    }

    /**
     * Used when adding a new item record into database.
     * @param username the user who owns the item
     * @param description description of the item
     * @param deadline deadline of the item
     */
    SchedulerItem(String username, String description, Date deadline) {
        super("SchedulerItem");
        Entity itemEntity = getNewEntity();
        itemEntity.setProperty("username", username);
        this.description = description;
        itemEntity.setProperty("description", description);
        this.deadline = deadline;
        itemEntity.setProperty("deadline", deadline);
        putIntoDatabase(itemEntity);
    }

    /**
     * Obtain a unique id of the scheduler item object from data store.
     * @return key string
     */
    public String getKeyString() {
        return KeyFactory.keyToString(key);
    }

    public String getDescription() {
        return description;
    }

    /**
     * Obtain the string form of the deadline.
     * @return deadline for the scheduler item
     */
    public String getDeadline() {
        return dateFormatter(deadline);
    }

    /**
     * Change the deadline of a project item.
     * This will also change the entity in the database.
     * @param deadline new deadline
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
        Entity existingItemEntity = getEntityByKey(key);
        existingItemEntity.setProperty("deadline", deadline);
        putIntoDatabase(existingItemEntity);
    }

    @Override
    protected SimpleDateFormat getDateFormatter() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return formatter;
    }
}