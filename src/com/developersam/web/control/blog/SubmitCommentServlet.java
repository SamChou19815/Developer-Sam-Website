package com.developersam.web.control.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;
import com.developersam.web.model.blog.exceptions.BlogCommentActionPermissionDenied;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process user's request to add a comment.
 */
@WebServlet(name = "SubmitCommentServlet", value="/apps/blog/submitComment")
public class SubmitCommentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getParameter("url");
        boolean anonymous = Boolean.parseBoolean(request.getParameter("anonymous"));
        String content = request.getParameter("comment");
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
        response.getWriter().print(output);
        response.setCharacterEncoding("UTF-8");
    }
}