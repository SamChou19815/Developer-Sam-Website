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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String url = req.getParameter("url");
        BlogArticle blogArticle;
        List<BlogComment> blogComments;
        String title;
        try {
            blogArticle = new BlogArticle(url);
            blogArticle.frequencyPlusOne(getIPAddress(req));
            title = blogArticle.getTitle();
            blogComments = blogArticle.getComments();
        } catch (BlogArticleNotFoundException e) {
            resp.sendRedirect("/errors/404.jsp");
            return;
        }
        req.setAttribute("title", title);
        req.setAttribute("article", blogArticle);
        req.setAttribute("comments", blogComments);
        req.getRequestDispatcher("/blogArticle.jsp").forward(req, resp);
    }
    
}