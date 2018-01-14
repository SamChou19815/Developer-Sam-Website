package com.developersam.web.controller.ten

import com.developersam.web.model.ten.TenClientMove
import com.developersam.web.util.GsonUtil

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that returns AI's response to human's move in board game TEN.
 */
@WebServlet("/apis/ten/response")
class GameResponseServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val gson = GsonUtil.GSON
        val clientMove = gson.fromJson(req.reader, TenClientMove::class.java)
        val response = TenController.respond(clientMove = clientMove)
        gson.toJson(response, resp.writer)
    }

}
