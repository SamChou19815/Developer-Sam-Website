package com.developersam.web.model.blog;

import com.developersam.web.model.blog.util.BlogArticleStatistics;
import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection class that fetches all blog articles.
 */
public class BlogArticles extends DataStoreObject {

    /**
     * Initialize the object by stating it relates to BlogArticle entity in DataStore
     */
    public BlogArticles() {
        super("BlogArticle");
    }

    /**
     * Get list of blog articles in descending order.
     * @return blog article list
     */
    public List<BlogArticle> getArticles() {
        List<BlogArticle> blogArticleList = new ArrayList<>();
        Query q = getQuery().addSort("published", Query.SortDirection.DESCENDING);
        PreparedQuery pq = getPreparedQuery(q);
        for (Entity blogArticleEntity : pq.asIterable()) {
            blogArticleList.add(new BlogArticle(blogArticleEntity));
        }
        return blogArticleList;
    }

    /**
     * Increase an IP address's visiting frequency by 1 for the main page of blog article
     * @param ip ip address
     */
    public void frequencyPlusOne(String ip) {
        new BlogArticleStatistics("main").usagePlusOne(ip);
    }

    /**
     * Obtain number of unique visitors for the main page of blog article
     * @return number of unique visitors
     */
    public long getNumberOfVisitors() {
        return new BlogArticleStatistics("main").getNumberOfVisitors();
    }
}