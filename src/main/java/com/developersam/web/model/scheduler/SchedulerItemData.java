package com.developersam.web.model.scheduler;

import com.developersam.web.util.DataStoreObject;
import com.developersam.web.util.DateUtil;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserServiceFactory;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * A simple data class of scheduler item that can be easily written into
 * database.
 * It is also a simplified object for JSON transmission.
 */
public class SchedulerItemData extends DataStoreObject {
    
    /**
     * Key string of the item, which may be {@code null}.
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
     * Private no-arg constructor for GSON.
     */
    private SchedulerItemData() {
        super("SchedulerItem");
        keyString = null;
        description = null;
        deadline = null;
    }
    
    /**
     * A helper method to check the sanity of the data.
     *
     * @return whether the input data is clean and safe to put into database. If
     * so, give back an entity, else give back {@code null}.
     */
    @Nullable
    private Entity sanityCheck() {
        if (description == null || deadline == null
                || description.trim().equals("")
                || deadline.compareTo(DateUtil.getYesterday()) < 0) {
            return null;
        }
        if (keyString == null) {
            return getNewEntity();
        } else {
            return getEntityByKey(keyString);
        }
    }
    
    /**
     * Write the current record into the database, if it passed the sanity
     * check.
     *
     * @return whether the item has been put into the database.
     */
    public boolean writeToDatabase() {
        Entity itemEntity = sanityCheck();
        if (itemEntity == null) {
            return false;
        }
        String userEmail = UserServiceFactory.getUserService()
                .getCurrentUser().getEmail();
        itemEntity.setProperty("userEmail", userEmail);
        itemEntity.setProperty("description", description);
        itemEntity.setProperty("deadline", deadline);
        itemEntity.setProperty("completed", false);
        putIntoDatabase(itemEntity);
        return true;
    }
    
}
