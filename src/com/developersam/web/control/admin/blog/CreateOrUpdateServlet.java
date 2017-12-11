package com.developersam.web.control.admin.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogContentNotFetchedException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process admin's request to add or update a blog article
 */
@WebServlet(name = "CreateOrUpdateServlet",
        value = "/apps/admin/blog/createOrUpdate")
public class CreateOrUpdateServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String path = getServletContext().getRealPath("/");
        String url = request.getParameter("url");
        String title = request.getParameter("title");
        try {
            new BlogArticle(url, title, path);
            response.getWriter().print("Success!");
        } catch (BlogContentNotFetchedException e) {
            response.getWriter().print("Blog content not fetched!");
        }
        response.setCharacterEncoding("UTF-8");
    }
    
}