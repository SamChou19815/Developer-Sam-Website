package com.developersam.web.control.admin.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process admin's request to load content of blog article to
 * preview.
 */
@WebServlet(name = "LoadContentServlet", value = "/apps/admin/blog/loadContent")
public class LoadContentServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String url = req.getParameter("url");
        try {
            BlogArticle blogArticle = new BlogArticle(url);
            resp.getWriter().print(blogArticle.getContent());
        } catch (BlogArticleNotFoundException e) {
            resp.getWriter().print("Blog article not found!");
        }
        resp.setCharacterEncoding("UTF-8");
    }
    
}