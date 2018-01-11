package com.developersam.web.model.scheduler;

import com.developersam.web.util.DataStoreObject;
import com.developersam.web.util.DateUtil;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;
import static com.google.appengine.api.datastore.Query.FilterOperator.GREATER_THAN_OR_EQUAL;

/**
 * Represent the scheduler app.
 */
public class Scheduler extends DataStoreObject {
    
    /**
     * The only instance of itself.
     */
    public static final Scheduler INSTANCE = new Scheduler();
    
    /**
     * A private default no-arg constructor.
     */
    private Scheduler() {
        super("SchedulerItem");
    }
    
    /**
     * Obtain a list of all scheduler items for a user signed in.
     *
     * @return a list of scheduler items
     */
    public List<SchedulerItem> getAllSchedulerItems() {
        String userEmail = UserServiceFactory.getUserService()
                .getCurrentUser().getEmail();
        Filter filterUser = new FilterPredicate(
                "userEmail", EQUAL, userEmail);
        Filter filterDeadline = new FilterPredicate(
                "deadline", GREATER_THAN_OR_EQUAL,
                DateUtil.getYesterday());
        List<Boolean> trueAndFalse = new ArrayList<>(2);
        trueAndFalse.add(true);
        trueAndFalse.add(false);
        // Just to overcome Datastore's indexing and sorting limitation.
        Filter filterCompleted = new FilterPredicate(
                "completed", FilterOperator.IN, trueAndFalse);
        Filter filter = CompositeFilterOperator.and(
                filterCompleted, filterUser, filterDeadline);
        Query q = getQuery().
                setFilter(filter).
                addSort("completed", SortDirection.ASCENDING).
                addSort("deadline", SortDirection.ASCENDING);
        PreparedQuery pq = getPreparedQuery(q);
        return StreamSupport.stream(pq.asIterable().spliterator(), false)
                .map(SchedulerItem::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a scheduler item with a given key.
     *
     * @param key key of the item to be deleted.
     */
    public void delete(String key) {
        SchedulerItem item = SchedulerItem.from(key);
        if (item != null) {
            item.delete();
        }
    }
    
    /**
     * Change the completion status for a given scheduler item.
     *
     * @param key key of the item.
     * @param complete completion status.
     */
    public void changeCompletionStatus(String key, boolean complete) {
        SchedulerItem item = SchedulerItem.from(key);
        if (item != null) {
            item.markAs(complete);
        }
    }
    
}