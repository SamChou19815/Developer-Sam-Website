package com.developersam.web.model.scheduler;

import com.developersam.web.util.DataStoreObject;
import com.developersam.web.util.DateUtil;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * An individual item in the scheduler.
 * It consists of description, deadline, and a completion status.
 */
public class SchedulerItem extends DataStoreObject {
    
    /**
     * The datastore entity of the item.
     */
    private transient final Entity entity;
    /**
     * The key string of the entity.
     */
    private final String keyString;
    /**
     * Description of the item.
     */
    private final String description;
    /**
     * Deadline of the item.
     */
    private final Date deadline;
    /**
     * Compute days left.
     */
    private final int daysLeft;
    /**
     * Whether the item has been completed.
     */
    private final boolean completed;
    
    /**
     * Construct itself from an entity fetched from database.
     *
     * @param projectEntity a project entity.
     */
    SchedulerItem(Entity projectEntity) {
        super("SchedulerItem");
        entity = projectEntity;
        keyString = KeyFactory.keyToString(projectEntity.getKey());
        description = (String) projectEntity.getProperty("description");
        deadline = (Date) projectEntity.getProperty("deadline");
        daysLeft = calculateDaysLeft(deadline);
        completed = (boolean) projectEntity.getProperty("completed");
    }
    
    /**
     * Construct a scheduler item from a unique key string.
     *
     * @param keyString a unique key string.
     * @return the constructed scheduler item, which can be null if the key
     * given is invalid.
     */
    @Nullable
    static SchedulerItem from(String keyString) {
        Entity entity = getEntityByKey(keyString);
        if (entity == null) {
            return null;
        }
        return new SchedulerItem(entity);
    }
    
    /**
     * Obtain a unique id of the scheduler item object from data store.
     *
     * @return key string.
     */
    public String getKeyString() {
        return keyString;
    }
    
    /**
     * Obtain the description of the scheduler item.
     *
     * @return the description of the scheduler item.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtain the string form of the deadline.
     *
     * @return deadline for the scheduler item
     */
    public String getDeadline() {
        return DateUtil.dateToString(deadline);
    }
    
    /**
     * Calculate and obtain how many days left for the deadline.
     *
     * @param deadline deadline date.
     * @return days left.
     */
    private static int calculateDaysLeft(Date deadline) {
        long diff = deadline.getTime() - new Date().getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(diff) + 1;
    }
    
    /**
     * Calculate and obtain how many days left for the deadline.
     *
     * @return days left.
     */
    public int getDaysLeft() {
        return daysLeft;
    }
    
    /**
     * Report whether the scheduler item is completed.
     *
     * @return whether the scheduler item is completed.
     */
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * Delete the item from scheduler database.
     */
    public void delete() {
        removeFromDatabase(entity.getKey());
    }
    
    /**
     * Mark the item as completed or not.
     *
     * @param completed whether the item should be marked as completed or not.
     */
    void markAs(boolean completed) {
        entity.setProperty("completed", completed);
        putIntoDatabase(entity);
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("entity", entity)
                .add("keyString", keyString)
                .add("description", description)
                .add("deadline", deadline)
                .add("daysLeft", daysLeft)
                .add("completed", completed)
                .toString();
    }
    
}