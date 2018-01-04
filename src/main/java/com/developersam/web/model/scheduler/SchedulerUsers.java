package com.developersam.web.model.scheduler;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;

/**
 * Manages the collection of all the scheduler users in the database.
 */
public class SchedulerUsers extends DataStoreObject {
    
    /**
     * The only instance of itself.
     */
    private static final SchedulerUsers INSTANCE = new SchedulerUsers();
    
    /**
     * Construct itself privately.
     */
    private SchedulerUsers() {
        super("SchedulerUser");
    }

    /**
     * Send the notifications to all the users if they choose to have email
     * notification.
     * This method is only called internally.
     */
    private void sendEmailNotificationsInternal() {
        PreparedQuery pq = getPreparedQuery(getQuery());
        for (Entity userEntity: pq.asIterable()) {
            new SchedulerUser(userEntity).sendEmailNotification();
        }
    }
    
    /**
     * Send the notifications to all the users if they choose to have email
     * notification.
     */
    public static void sendEmailNotifications() {
        INSTANCE.sendEmailNotificationsInternal();
    }

}