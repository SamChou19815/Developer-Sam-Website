package com.developersam.web.model.statistics;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.developersam.web.model.datastore.DataStoreObject;

import java.util.stream.StreamSupport;

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;

/**
 * A statistics class designed to track google user's usage of an app.
 * It requires user to sign in to google account.
 */
public class UserStatistics extends DataStoreObject {
    
    /**
     * The name of the app.
     */
    private final String appName;
    /**
     * The filter to filter out other apps.
     */
    private final Filter filterAppName;
    
    /**
     * Construct a UserStatistics object by app name.
     * An UserStatistics object is used only for one app only.
     *
     * @param appName app name.
     */
    public UserStatistics(String appName) {
        super("UserStatistics");
        this.appName = appName;
        this.filterAppName = new FilterPredicate(
                "appName", EQUAL, appName);
    }
    
    /**
     * Given a user, increase his/her app usage frequency by 1.
     *
     * @param user a google user.
     */
    public void usagePlusOne(User user) {
        Filter filterUser = new FilterPredicate(
                "user", EQUAL, user.getNickname());
        Filter filter = CompositeFilterOperator.and(filterAppName, filterUser);
        Query q = getQuery().setFilter(filter);
        PreparedQuery pq = getPreparedQuery(q);
        Entity userUsageEntity = pq.asSingleEntity();
        if (userUsageEntity == null) {
            userUsageEntity = getNewEntity();
            userUsageEntity.setProperty("appName", appName);
            userUsageEntity.setProperty("user", user.getNickname());
            userUsageEntity.setProperty("frequency", 1);
        } else {
            long frequency = (long) userUsageEntity.getProperty("frequency");
            frequency++;
            userUsageEntity.setProperty("frequency", frequency);
        }
        putIntoDatabase(userUsageEntity);
    }
    
    /**
     * Obtain the total number of usages of the app.
     *
     * @return the total number of usages of the app.
     */
    public long getTotalUsage() {
        Filter filterAppName = new FilterPredicate(
                "appName", EQUAL, appName);
        Query query = getQuery().setFilter(filterAppName);
        PreparedQuery pq = getPreparedQuery(query);
        return StreamSupport.stream(pq.asIterable().spliterator(), false)
                .mapToLong(entity -> (long) entity.getProperty("frequency"))
                .sum();
    }
    
}