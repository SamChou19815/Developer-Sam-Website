package com.developersam.web.control.blog;

import com.developersam.web.control.common.IPStatisticsServlet;
import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;
import com.developersam.web.model.blog.BlogComment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * A servlet that loads blog article object and some of its info onto
 * blogArticle.jsp.
 */
@WebServlet(name = "BlogArticleServlet", value = "/blogArticle")
public class BlogArticleServlet extends IPStatisticsServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getParameter("url");
        BlogArticle blogArticle;
        List<BlogComment> blogComments;
        String title;
        try {
            blogArticle = new BlogArticle(url);
            blogArticle.frequencyPlusOne(getIPAddress(request));
            title = blogArticle.getTitle();
            blogComments = blogArticle.getComments();
        } catch (BlogArticleNotFoundException e) {
            response.sendRedirect("/errors/404.jsp");
            return;
        }
        request.setAttribute("title", title);
        request.setAttribute("article", blogArticle);
        request.setAttribute("comments", blogComments);
        request.getRequestDispatcher("/blogArticle.jsp")
                .forward(request, response);
    }
    
}