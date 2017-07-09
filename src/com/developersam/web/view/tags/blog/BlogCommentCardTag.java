package com.developersam.web.view.tags.blog;

import com.developersam.web.devsuit.tags.components.button.LinkButtonTag;
import com.developersam.web.devsuit.tags.components.card.CardActionsTag;
import com.developersam.web.devsuit.tags.components.card.CardTag;
import com.developersam.web.devsuit.tags.components.card.CardTextBorderedTag;
import com.developersam.web.devsuit.tags.components.card.CardTextTag;
import com.developersam.web.model.blog.BlogComment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * A blog comment card.
 */
public class BlogCommentCardTag extends CardTag {

    private UserService userService = UserServiceFactory.getUserService();
    // used to check whether the user can delete a comment
    private BlogComment blogComment;

    /**
     * Initialize the tag by a blog comment object that contains all the necessary information.
     * @param blogComment blog comment object
     */
    public void setBlogComment(BlogComment blogComment) {
        this.blogComment = blogComment;
        setId("comment-"+blogComment.getKey());
        setTitle("Comment");
        setCustomClasses("blogCommentCard");
    }

    /**
     * Print comment info, including publication and author info
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printCommentInfo() throws JspException, IOException {
        CardTextBorderedTag cardTextTagForCommentInfo = new CardTextBorderedTag();
        cardTextTagForCommentInfo.setParentTag(this);
        final String authorLine = blogComment.isAnonymous()? "" :
                "<span class='blogInfo'>Author: " + blogComment.getUsername() + "</span>";
        final String publishedLine = "<span class='blogInfo'>Published: " + blogComment.getPublished() + "</span>";
        cardTextTagForCommentInfo.setBodyContent(authorLine + publishedLine);
        cardTextTagForCommentInfo.doTag();
    }

    /**
     * Print comment content, with content formatted.
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printCommentContent() throws JspException, IOException {
        CardTextTag cardTextTagForCommentContent = new CardTextTag();
        cardTextTagForCommentContent.setParentTag(this);
        String comment = blogComment.getContent();
        comment = comment.replaceAll("(\r\n|\n)", "<br>"); // format content
        cardTextTagForCommentContent.setBodyContent(comment);
        cardTextTagForCommentContent.doTag();
    }

    /**
     * Print button to delete the comment
     * @throws JspException jsp exception
     * @throws IOException io exception
     */
    private void printDeleteCommentAction() throws JspException, IOException {
        CardActionsTag actionsTag = new CardActionsTag();
        actionsTag.setParentTag(this);
        LinkButtonTag linkButtonTag = new LinkButtonTag();
        linkButtonTag.setHref("#");
        linkButtonTag.setOpenInNewTab(false);
        linkButtonTag.setOnClick("Controller.deleteComment('" + blogComment.getKey() + "')");
        linkButtonTag.setBodyContent("Delete");
        actionsTag.addChildrenTag(linkButtonTag);
        actionsTag.doTag();
    }

    @Override
    protected void printBodyContent() throws JspException, IOException {
        printTitle();
        printCommentInfo();
        printCommentContent();
        if (userService.isUserLoggedIn() &&
                userService.getCurrentUser().getNickname().equals(blogComment.getUsername())) {
            // if the user publishes the comment, he or she can delete it.
            printDeleteCommentAction();
        }
    }
}