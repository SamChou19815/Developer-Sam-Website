package com.developersam.controller.chunkreader

import com.developersam.model.chunkreader.AnalyzedArticle
import com.developersam.webcore.gson.gson
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that loads all the detailed information for a given article's key.
 */
@WebServlet("/apis/chunkreader/articleDetail")
class ArticleDetailServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val key: String? = req.getParameter("key")
        val article: AnalyzedArticle? = if (key == null) {
            null
        } else {
            AnalyzedArticle.from(keyString = key)
        }
        gson.toJson(article, resp.writer)
    }

}