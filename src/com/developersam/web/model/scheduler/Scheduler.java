package com.developersam.web.model.scheduler;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Scheduler extends DataStoreObject {

    private UserService userService;

    public Scheduler() {
        super("SchedulerItem");
        userService = UserServiceFactory.getUserService();
    }

    public List<SchedulerItem> getAllSchedulerItems() {
        List<SchedulerItem> schedulerItems = new ArrayList<>();
        String username = userService.getCurrentUser().getNickname();
        Filter filterUser = new FilterPredicate("username", FilterOperator.EQUAL, username);
        Filter filterDeadline = new FilterPredicate("deadline", FilterOperator.GREATER_THAN, new Date());
        List<Boolean> trueAndFalse = new ArrayList<>(2);
        trueAndFalse.add(true);
        trueAndFalse.add(false);
        Filter filterCompleted = new FilterPredicate("completed", FilterOperator.IN, trueAndFalse);
        Filter filter = CompositeFilterOperator.and(filterCompleted, filterUser, filterDeadline);
        Query q = getQuery().
                setFilter(filter).
                addSort("completed", SortDirection.ASCENDING).
                addSort("deadline", SortDirection.ASCENDING);
        PreparedQuery pq = getPreparedQuery(q);
        for (Entity itemEntity: pq.asIterable()) {
            SchedulerItem schedulerItem = new SchedulerItem(itemEntity);
            schedulerItems.add(schedulerItem);
        }
        return schedulerItems;
    }

    @Override
    protected SimpleDateFormat getDateFormatter() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return formatter;
    }

    public boolean addItem(String description, String deadline) {
        if (description.equals("")) {
            return false;
        }
        try {
            Date deadlineDate = dateFormatter(deadline);
            if (deadlineDate.compareTo(new Date()) > 0) {
                new SchedulerItem(userService.getCurrentUser().getNickname(), description, deadlineDate);
                return true;
            }else {
                return false;
            }
        }catch (ParseException e) {
            return false;
        }
    }

    public void delete(String key) {
        new SchedulerItem(getEntityByKey(key)).delete();
    }

    public void changeCompletionStatus(String key, boolean complete) {
        SchedulerItem schedulerItem = new SchedulerItem(getEntityByKey(key));
        if (complete) {
            schedulerItem.markAsCompleted();
        }else {
            schedulerItem.markAsUncompleted();
        }
    }

}