package com.developersam.web.view.tags.blog;

import com.developersam.web.devsuit.tags.components.button.LinkButtonTag;
import com.developersam.web.devsuit.tags.components.card.CardActionsTag;
import com.developersam.web.devsuit.tags.components.card.CardTag;
import com.developersam.web.devsuit.tags.components.card.CardTextTag;
import com.developersam.web.devsuit.tags.components.input.CheckboxInputTag;
import com.developersam.web.devsuit.tags.components.input.TextAreaInputTag;
import com.developersam.web.devsuit.tags.components.loading.ProgressBarIntermediateTag;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * A card to add comment or to login to do so
 */
public class BlogAddCommentCardTag extends CardTag {

    private UserService userService = UserServiceFactory.getUserService();

    private String url;

    /**
     * Set url of the blog article
     * @param url url segment of blog article, used to redirect.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public BlogAddCommentCardTag() {
        setTitle("Add your comment");
    }

    @Override
    protected void printBodyContent() throws JspException, IOException {
        printTitle();
        if (userService.isUserLoggedIn()) {
            printAddComment();
        }else {
            printLogin();
        }
    }

    /**
     * Print add comment card
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printAddComment()  throws JspException, IOException {
        // print comment box
        CardTextTag cardTextTag = new CardTextTag();
        cardTextTag.setParent(this);
        TextAreaInputTag textAreaInputTag = new TextAreaInputTag();
        textAreaInputTag.setId("newComment");
        textAreaInputTag.setRows(5);
        textAreaInputTag.setBodyContent("Add your comment.");
        CheckboxInputTag checkboxInputTag = new CheckboxInputTag();
        checkboxInputTag.setId("anonymous");
        checkboxInputTag.setBodyContent("Keep me anonymous.");
        ProgressBarIntermediateTag progressBarIntermediateTag = new ProgressBarIntermediateTag();
        progressBarIntermediateTag.setId("progress-submit-comment");
        cardTextTag.addChildrenTag(textAreaInputTag);
        cardTextTag.addChildrenTag(checkboxInputTag);
        cardTextTag.addChildrenTag(progressBarIntermediateTag);
        cardTextTag.doTag();
        // print submit button
        CardActionsTag cardActionsTag = new CardActionsTag();
        cardActionsTag.setParent(this);
        LinkButtonTag linkButtonTag = new LinkButtonTag();
        linkButtonTag.setHref("#");
        linkButtonTag.setOpenInNewTab(false);
        linkButtonTag.setOnClick("Controller.submitComment();");
        linkButtonTag.setBodyContent("Submit Comment");
        cardActionsTag.addChildrenTag(linkButtonTag);
        cardActionsTag.doTag();
    }

    /**
     * Print login card
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printLogin()  throws JspException, IOException {
        // print login link button
        CardActionsTag cardActionsTag = new CardActionsTag();
        cardActionsTag.setParent(this);
        LinkButtonTag linkButtonTag = new LinkButtonTag();
        String currentURL = "/blogArticle?url=" + url;
        linkButtonTag.setHref(userService.createLoginURL(currentURL));
        linkButtonTag.setOpenInNewTab(false);
        linkButtonTag.setBodyContent("Login with Google Account to comment");
        cardActionsTag.addChildrenTag(linkButtonTag);
        cardActionsTag.doTag();
    }
}