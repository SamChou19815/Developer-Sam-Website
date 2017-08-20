package com.developersam.web.model.scheduler;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
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

    public List<SchedulerItem> getAllUnfinishedSchedulerItems() {
        List<SchedulerItem> schedulerItems = new ArrayList<>();
        String username = userService.getCurrentUser().getNickname();
        Filter filterUser = new FilterPredicate("username", FilterOperator.EQUAL, username);
        Filter filterDeadline = new FilterPredicate("deadline", FilterOperator.GREATER_THAN, new Date());
        Filter filter = CompositeFilterOperator.and(filterUser, filterDeadline);
        Query q = getQuery().addSort("deadline", SortDirection.ASCENDING).setFilter(filter);
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

}