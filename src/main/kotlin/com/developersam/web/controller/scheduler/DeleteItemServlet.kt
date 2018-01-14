package com.developersam.web.controller.scheduler

import com.developersam.web.model.scheduler.Scheduler

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that delete an scheduler item.
 */
@WebServlet("/apis/scheduler/delete")
class DeleteItemServlet : HttpServlet() {

    override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        Scheduler.delete(key = req.getParameter("key"))
    }

}