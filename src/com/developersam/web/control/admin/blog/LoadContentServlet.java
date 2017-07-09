package com.developersam.web.control.admin.blog;

import com.developersam.web.model.blog.BlogArticle;
import com.developersam.web.model.blog.exceptions.BlogArticleNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet to process admin's request to load content of blog article to preview.
 */
public class LoadContentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getParameter("url");
        try {
            BlogArticle blogArticle = new BlogArticle(url);
            response.getWriter().print(blogArticle.getContent());
        }catch (BlogArticleNotFoundException e) {
            response.getWriter().print("Blog article not found!");
        }
        response.setCharacterEncoding("UTF-8");
    }

}