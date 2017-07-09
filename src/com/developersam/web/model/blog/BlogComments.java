package com.developersam.web.model.blog;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection class that fetches all blog comments associated with a blog article
 */
class BlogComments extends DataStoreObject {

    /**
     * Construct the class by blog article entity's key
     * @param parentKey parent key
     */
    BlogComments(Key parentKey) {
        super("BlogComment");
        setParentKey(parentKey);
    }

    /**
     * Obtain all comments associated with the blog article in ascending order
     * @return list of blog comments
     */
    List<BlogComment> getComments() {
        List<BlogComment> blogCommentsList = new ArrayList<>();
        Query q = getQuery().addSort("published", Query.SortDirection.ASCENDING);
        PreparedQuery pq = getPreparedQuery(q);
        for (Entity blogCommentEntity: pq.asIterable()) {
            blogCommentsList.add(new BlogComment(blogCommentEntity));
        }
        return blogCommentsList;
    }
}