package com.developersam.web.view.tags.blog;

/**
 * A blog article of full content, used in blog article page.
 */
public class BlogArticleContentCardTag extends BlogArticleCardTag {

    @Override
    void initializeBlogArticleCard() {
        blogInfo = "<span class='blogInfo'>Published: " + blogArticle.getPublished() + "</span>";
        String updated = blogArticle.getUpdated();
        if (updated != null) {
            blogInfo += "<span class='blogInfo'>Updated: " + updated + "</span>";
        }
        blogContent = blogArticle.getContent();
    }
}
