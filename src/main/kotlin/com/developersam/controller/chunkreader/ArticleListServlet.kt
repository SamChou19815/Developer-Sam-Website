package com.developersam.controller.chunkreader

import com.developersam.model.chunkreader.AnalyzedArticles
import com.developersam.webcore.gson.gson
import com.google.appengine.api.users.UserServiceFactory
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that loads list of articles submitted by the user, or gives a login
 * URL to the user.
 */
@WebServlet("/apis/chunkreader/articleList")
class ArticleListServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val userService = UserServiceFactory.getUserService()
        if (userService.isUserLoggedIn) {
            gson.toJson(AnalyzedArticles.asList, resp.writer)
        } else {
            resp.writer.print(
                    "url: " + userService.createLoginURL("/scheduler"))
        }
    }

}