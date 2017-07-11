package com.developersam.web.view.tags.blog;

import com.developersam.web.devsuit.tags.components.card.CardTag;
import com.developersam.web.devsuit.tags.components.card.CardTextBorderedTag;
import com.developersam.web.devsuit.tags.components.card.CardTextTag;
import com.developersam.web.model.blog.BlogArticle;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * An abstract blog article card.
 */
public abstract class BlogArticleCardTag extends CardTag {

    BlogArticle blogArticle;
    String blogInfo;
    String blogContent;

    /**
     * Initialize the tag by a blog article object, which contains all the necessary information
     * @param blogArticle blog article object
     */
    public void setBlogArticle(BlogArticle blogArticle) {
        this.blogArticle = blogArticle;
        setTitle(blogArticle.getTitle());
        setCustomClasses("blogArticleCard");
        initializeBlogArticleCard();
    }

    /**
     * Initialize blog info and blog content.
     * The initialization is different for different kind of blog cards, so the method is abstract.
     */
    abstract void initializeBlogArticleCard();

    /**
     * Print blog info, which includes publication date, ...
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printBlogInfo() throws JspException, IOException {
        CardTextBorderedTag cardTextTagForBlogInfo = new CardTextBorderedTag();
        cardTextTagForBlogInfo.setParent(this);
        cardTextTagForBlogInfo.setBodyContent(blogInfo);
        cardTextTagForBlogInfo.doTag();
    }

    /**
     * Print blog content, which is entire or part of article
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printBlogContent() throws JspException, IOException {
        CardTextTag cardTextTagForBlogContent = new CardTextTag();
        cardTextTagForBlogContent.setParent(this);
        cardTextTagForBlogContent.setCustomClasses("blogArticleContent");
        cardTextTagForBlogContent.setBodyContent(blogContent);
        cardTextTagForBlogContent.doTag();
    }

    @Override
    protected void printBodyContent() throws JspException, IOException {
        printTitle();
        printBlogInfo();
        printBlogContent();
    }
}