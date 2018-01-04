package com.developersam.web.control.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;
import com.developersam.web.model.blog.exceptions.BlogCommentActionPermissionDenied;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process user's request to add a comment.
 */
@WebServlet(name = "SubmitCommentServlet", value = "/apps/blog/submitComment")
public class SubmitCommentServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String url = req.getParameter("url");
        boolean anonymous = Boolean.parseBoolean(
                req.getParameter("anonymous"));
        String content = req.getParameter("comment");
        UserService userService = UserServiceFactory.getUserService();
        String output;
        try {
            BlogArticle blogArticle = new BlogArticle(url);
            blogArticle.addComment(userService, anonymous, content);
            output = "success";
        } catch (BlogArticleNotFoundException e) {
            output = "Blog article not found!";
        } catch (BlogCommentActionPermissionDenied e) {
            output = "Permission denied!";
        }
        resp.getWriter().print(output);
        resp.setCharacterEncoding("UTF-8");
    }
    
}