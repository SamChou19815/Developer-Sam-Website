package com.developersam.web.model.blog;

import com.developersam.web.model.datastore.DataStoreObject;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import java.util.Date;

/**
 * Blog comment object.
 * It serves both to add a blog comment and retrieve info to be used by custom tags.
 */
public class BlogComment extends DataStoreObject {

    private String key;
    private String username;
    private Date published;
    private boolean anonymous;
    private String content;

    /**
     * Construct the blog comment by a known blog comment entity.
     * This is used when processing fetched comments.
     * @param blogCommentEntity blog comment entity
     */
    BlogComment(Entity blogCommentEntity) {
        key = KeyFactory.keyToString(blogCommentEntity.getKey());
        username = (String) blogCommentEntity.getProperty("username");
        published = (Date) blogCommentEntity.getProperty("published");
        anonymous = (boolean) blogCommentEntity.getProperty("anonymous");
        content = textToString(blogCommentEntity.getProperty("content"));
    }

    /**
     * Construct the blog comment by all of its construction information
     * @param parentKey key of parent entity: a blog article
     * @param username user that posts the comment
     * @param anonymous whether to keep the comment anonymous
     * @param content content of the comment
     */
    BlogComment(Key parentKey, String username, boolean anonymous, String content) {
        super("BlogComment");
        setParentKey(parentKey);
        this.username = username;
        published = new Date();
        this.anonymous = anonymous;
        this.content = content;
        Entity blogCommentEntity = getNewEntity();
        blogCommentEntity.setProperty("username", username);
        blogCommentEntity.setProperty("published", published);
        blogCommentEntity.setProperty("anonymous", anonymous);
        blogCommentEntity.setProperty("content", new Text(content));
        putIntoDatabase(blogCommentEntity);
        this.key = KeyFactory.keyToString(blogCommentEntity.getKey());
    }

    /**
     * Obtain the key of the comment
     * @return comment key
     */
    public String getKey() {
        return key;
    }

    /**
     * Obtain who posts the comment
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtain the time of publication
     * @return publication time in string
     */
    public String getPublished() {
        return dateFormatter(published);
    }

    /**
     * Know whether the id of poster should be kept anonymous
     * @return whether to keep it anonymous
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Obtain the content of the comment
     * @return comment content
     */
    public String getContent() {
        return content;
    }
}