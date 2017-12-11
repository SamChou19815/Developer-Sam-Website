package com.developersam.web.control.blog;

import com.developersam.web.control.common.IPStatisticsServlet;
import com.developersam.web.model.blog.BlogArticles;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that loads list of blog articles onto blog.jsp.
 */
@WebServlet(name = "BlogArticlesServlet", value = "/blog")
public class BlogArticlesServlet extends IPStatisticsServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        BlogArticles blogArticles = new BlogArticles();
        blogArticles.frequencyPlusOne(getIPAddress(request));
        request.setAttribute(
                "blogArticles", blogArticles.getArticles());
        request.getRequestDispatcher("/blog.jsp")
                .forward(request, response);
    }
    
}