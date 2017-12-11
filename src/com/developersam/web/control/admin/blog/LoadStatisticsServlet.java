package com.developersam.web.control.admin.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.BlogArticles;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process admin's request to load statistics of blog articles.
 */
@WebServlet(name = "LoadStatisticsServlet",
        value = "/apps/admin/blog/loadStatistics")
public class LoadStatisticsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        BlogArticles blogArticles = new BlogArticles();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("\"main\",");
        sb.append(blogArticles.getNumberOfVisitors());
        for (BlogArticle blogArticle : blogArticles.getArticles()) {
            String title = blogArticle.getTitle();
            if (title.length() > 30) {
                title = title.substring(0, 30) + "...";
            }
            long count = blogArticle.getNumberOfVisitors();
            sb.append(",");
            sb.append("\"");
            sb.append(title);
            sb.append("\",");
            sb.append(count);
        }
        sb.append("]");
        response.getWriter().print(sb.toString());
        response.setCharacterEncoding("UTF-8");
    }
    
}