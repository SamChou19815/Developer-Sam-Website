package com.developersam.web.model.blog;

import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;
import com.developersam.web.model.blog.exceptions.BlogCommentActionPermissionDenied;
import com.developersam.web.model.blog.exceptions.BlogContentNotFetchedException;
import com.developersam.web.model.blog.util.BlogArticleStatistics;
import com.developersam.web.model.blog.util.BlogContentFetcher;
import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.UserService;

import java.util.*;

/**
 * Blog article object.
 * It serves both to add a blog article and retrieve info to be used by
 * custom tags.
 */
public class BlogArticle extends DataStoreObject {
    
    /**
     * Record the key of the blog article.
     */
    private Key key;
    /**
     * Basic information about the blog article.
     */
    private String url, title;
    /**
     * Date of publishing and update.
     */
    private Date published, updated;
    /**
     * Summary and content of the blog article.
     */
    private String summary, content;
    
    /**
     * Used when the blog article entity is already found
     *
     * @param blogArticleEntity blog article entity
     */
    BlogArticle(Entity blogArticleEntity) {
        super("BlogArticle");
        constructBlogArticleByEntity(blogArticleEntity);
    }
    
    /**
     * Used when needs to read a blog article
     *
     * @param url file url segment
     */
    public BlogArticle(String url) throws BlogArticleNotFoundException {
        super("BlogArticle");
        this.url = url;
        constructBlogArticleInViewMode();
    }
    
    /**
     * Used when needs to create or update a blog article
     *
     * @param url file url segment
     * @param title title
     * @param path root path
     * @throws BlogContentNotFetchedException error
     */
    public BlogArticle(String url, String title, String path)
            throws BlogContentNotFetchedException {
        super("BlogArticle");
        this.url = url;
        this.title = title;
        constructBlogArticleInCreateOrUpdateMode(path);
    }
    
    /**
     * Get blog article entity, given url initialized during construction
     *
     * @return blog article entity
     */
    private Entity getBlogArticleEntity() {
        Query.Filter filterBlogUrl = new Query.
                FilterPredicate("url", Query.FilterOperator.EQUAL, url);
        Query q = getQuery().setFilter(filterBlogUrl);
        PreparedQuery pq = getPreparedQuery(q);
        return pq.asSingleEntity();
    }
    
    /**
     * Construct the class by a blog article entity
     *
     * @param blogArticleEntity blog article entity
     */
    private void constructBlogArticleByEntity(Entity blogArticleEntity) {
        key = blogArticleEntity.getKey();
        url = (String) blogArticleEntity.getProperty("url");
        title = (String) blogArticleEntity.getProperty("title");
        published = (Date) blogArticleEntity.getProperty("published");
        updated = (Date) blogArticleEntity.getProperty("updated");
        summary = textToString(blogArticleEntity.getProperty("summary"));
        content = ((Text) blogArticleEntity.getProperty("content")).getValue();
    }
    
    /**
     * Construct the blog article when users need to read it.
     *
     * @throws BlogArticleNotFoundException blog article not found given url
     */
    private void constructBlogArticleInViewMode()
            throws BlogArticleNotFoundException {
        Entity blogArticleEntity = getBlogArticleEntity();
        if (blogArticleEntity == null) {
            throw new BlogArticleNotFoundException();
        } else {
            constructBlogArticleByEntity(blogArticleEntity);
        }
    }
    
    /**
     * Construct the blog article when admin needs to create or update it
     *
     * @param path root path
     * @throws BlogContentNotFetchedException blog article not fetched
     */
    private void constructBlogArticleInCreateOrUpdateMode(String path)
            throws BlogContentNotFetchedException {
        String[] fetchedContent = BlogContentFetcher.fetch(path, url);
        String summary = fetchedContent[0], content = fetchedContent[1];
        Entity blogArticleEntity = getBlogArticleEntity();
        if (blogArticleEntity == null) {
            blogArticleEntity = getNewEntity();
            blogArticleEntity.setProperty("url", url);
            blogArticleEntity.setProperty("title", title);
            blogArticleEntity.setProperty("published", new Date());
            blogArticleEntity.setProperty("updated", null);
        } else {
            blogArticleEntity.setProperty("title", title);
            blogArticleEntity.setProperty("updated", new Date());
        }
        if (summary == null) {
            blogArticleEntity.setProperty("summary", null);
        } else {
            blogArticleEntity.setProperty("summary", new Text(summary));
        }
        blogArticleEntity.setProperty("content", new Text(content));
        putIntoDatabase(blogArticleEntity);
    }
    
    /**
     * Obtain url segment of the blog article.
     *
     * @return not the form url, but only the necessary segment that helps to
     * locate it.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Get the title of the blog article.
     *
     * @return title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Get the string representation of the time it is published.
     *
     * @return publication time.
     */
    public String getPublished() {
        return dateFormatter(published);
    }
    
    /**
     * Get the string representation of the time it is last updated.
     *
     * @return update time
     */
    public String getUpdated() {
        if (updated == null) {
            return null;
        }
        return dateFormatter(updated);
    }
    
    /**
     * Obtain summary of the blog article, can be null
     *
     * @return summary
     */
    public String getSummary() {
        return summary;
    }
    
    /**
     * Obtain content of the blog article
     *
     * @return content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Add a comment to a blog article
     *
     * @param userService used to find user login status
     * @param anonymous whether the comment is kept anonymous
     * @param content content of the comment
     * @throws BlogCommentActionPermissionDenied the user has not signed in
     */
    public void addComment(UserService userService,
                           boolean anonymous, String content)
            throws BlogCommentActionPermissionDenied {
        if (userService.isUserLoggedIn()) {
            String username = userService.getCurrentUser().getNickname();
            new BlogComment(key, username, anonymous, content);
        } else {
            throw new BlogCommentActionPermissionDenied();
        }
    }
    
    /**
     * Delete a comment from a blog article.
     *
     * @param userService used to find user identity.
     * @param commentKeyString key string of the comment.
     * @throws BlogCommentActionPermissionDenied the user has not signed in or
     * does not send the comment.
     */
    public void deleteComment(UserService userService, String commentKeyString)
            throws BlogCommentActionPermissionDenied {
        Key commentKey = KeyFactory.stringToKey(commentKeyString);
        BlogComment blogComment = new BlogComment(getEntityByKey(commentKey));
        if (userService.isUserLoggedIn() && userService.getCurrentUser()
                .getNickname().equals(blogComment.getUsername())) {
            // legal deletion from the same user
            removeFromDatabase(commentKey);
        } else {
            throw new BlogCommentActionPermissionDenied();
        }
    }
    
    /**
     * Get the number of comments associated with the article.
     * The method has cheaper cost than fetching all entities and then count
     *
     * @return number of comments
     */
    public int getCommentsNumber() {
        BlogComments blogComments = new BlogComments(key);
        return blogComments.getCommentsNumber();
    }
    
    /**
     * Get the comments associated with the article
     *
     * @return comment list
     */
    public List<BlogComment> getComments() {
        BlogComments blogComments = new BlogComments(key);
        return blogComments.getComments();
    }
    
    /**
     * Increase an IP address's visiting frequency by 1 for a blog article
     *
     * @param ip ip address
     */
    public void frequencyPlusOne(String ip) {
        new BlogArticleStatistics(url).usagePlusOne(ip);
    }
    
    /**
     * Obtain number of unique visitors for a blog article
     *
     * @return number of unique visitors
     */
    public long getNumberOfVisitors() {
        return new BlogArticleStatistics(url).getNumberOfVisitors();
    }
}