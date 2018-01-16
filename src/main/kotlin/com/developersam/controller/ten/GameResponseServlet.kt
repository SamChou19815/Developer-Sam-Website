package com.developersam.controller.ten

import com.developersam.ten.TenBoard
import com.developersam.ten.TenClientMove
import com.developersam.webcore.gson.gson
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
        val clientMove = gson.fromJson(req.reader, TenClientMove::class.java)
        val response = TenBoard.respond(clientMove = clientMove)
        gson.toJson(response, resp.writer)
    }

}
