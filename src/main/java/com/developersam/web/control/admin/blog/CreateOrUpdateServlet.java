package com.developersam.web.control.admin.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogContentNotFetchedException;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String path = getServletContext().getRealPath("/");
        String url = req.getParameter("url");
        String title = req.getParameter("title");
        try {
            new BlogArticle(url, title, path);
            resp.getWriter().print("Success!");
        } catch (BlogContentNotFetchedException e) {
            resp.getWriter().print("Blog content not fetched!");
        }
        resp.setCharacterEncoding("UTF-8");
    }
    
}