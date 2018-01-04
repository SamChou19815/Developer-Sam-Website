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
 * A servlet that processes user's request to delete a comment.
 */
@WebServlet(name = "DeleteCommentServlet", value = "/apps/blog/deleteComment")
public class DeleteCommentServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String url = req.getParameter("url");
        String key = req.getParameter("key");
        UserService userService = UserServiceFactory.getUserService();
        String output;
        try {
            BlogArticle blogArticle = new BlogArticle(url);
            blogArticle.deleteComment(userService, key);
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