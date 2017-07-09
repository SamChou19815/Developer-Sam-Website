package com.developersam.web.view.tags.blog;

import com.developersam.web.devsuit.tags.components.button.LinkButtonTag;
import com.developersam.web.devsuit.tags.components.card.CardActionsTag;
import com.developersam.web.devsuit.tags.components.card.CardTitleLinkedTag;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * A blog article of partial content, used in page that lists all blog articles.
 */
public class BlogArticleSummaryCardTag extends BlogArticleCardTag {

    private boolean shouldPrintReadMore;

    /**
     * Override the method to give the title a link
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    @Override
    protected void printTitle() throws JspException, IOException {
        CardTitleLinkedTag cardTitleLinkedTag = new CardTitleLinkedTag();
        cardTitleLinkedTag.setTitle(blogArticle.getTitle());
        cardTitleLinkedTag.setUrl("blogArticle?url="+blogArticle.getUrl());
        cardTitleLinkedTag.setParentTag(this);
        cardTitleLinkedTag.doTag();
    }

    @Override
    void initializeBlogArticleCard() {
        // blog info
        blogInfo = "<span class='blogInfo'>Published: " + blogArticle.getPublished() + "</span>";
        String updated = blogArticle.getUpdated();
        if (updated != null) {
            blogInfo += "<span class='blogInfo'>Updated: " + updated + "</span>";
        }
        blogInfo += "<span class='blogInfo'>Comments: " + blogArticle.getComments().size() + "</span>";
        // blog content
        blogContent = blogArticle.getSummary();
        if (blogContent == null) {
            blogContent = blogArticle.getContent();
            shouldPrintReadMore = false;
        }else {
            shouldPrintReadMore = true;
        }
    }

    /**
     * Override the method to add a read more link when necessary
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    @Override
    protected void printBodyContent() throws JspException, IOException {
        super.printBodyContent();
        if (shouldPrintReadMore) {
            CardActionsTag cardActionsTag = new CardActionsTag();
            cardActionsTag.setParentTag(this);
            LinkButtonTag linkButtonTag = new LinkButtonTag();
            linkButtonTag.setHref("blogArticle?url="+blogArticle.getUrl());
            linkButtonTag.setBodyContent("Read More");
            linkButtonTag.setOpenInNewTab(false);
            cardActionsTag.addChildrenTag(linkButtonTag);
            cardActionsTag.doTag();
        }
    }
}
