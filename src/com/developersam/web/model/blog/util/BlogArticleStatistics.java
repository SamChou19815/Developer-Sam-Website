package com.developersam.web.model.blog.util;

import com.developersam.web.model.statistics.IPStatistics;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * A subclass of ip statistics.
 * It specifically deals with blog related issues.
 */
public class BlogArticleStatistics extends IPStatistics {

    /**
     * Construct the class by blog article url.
     * @param url blog article url, served as app content of ip statistics.
     */
    public BlogArticleStatistics(String url) {
        super("blog", url);
    }

    /**
     * Get number of visitors.
     * @return number of unique ip visits to a blog article.
     */
    public long getNumberOfVisitors() {
        Query q = getQuery().setFilter(filter);
        PreparedQuery pq = getPreparedQuery(q);
        return pq.countEntities(FetchOptions.Builder.withLimit(1000));
    }

}