package com.developersam.web.view.tags.blog;

import com.developersam.web.devsuit.tags.components.head.HeadTag;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * The tag prints head of a web page, with additional blog css resources.
 */
public class BlogHeadTag extends HeadTag {

    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        printContent("<link href=\"https://fonts.googleapis.com/css?family=Roboto+Slab:300\" rel=\"stylesheet\">");
        printContent("<link rel=\"stylesheet\" href=\"/framework/css/blog.css?v=2\" />");
    }
}
