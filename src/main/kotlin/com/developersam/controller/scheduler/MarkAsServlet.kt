package com.developersam.controller.scheduler

import com.developersam.scheduler.Scheduler

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that mark an scheduler item as completed or uncompleted.
 */
@WebServlet("/apis/scheduler/markAs")
class MarkAsServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val key = req.getParameter("key") ?: return
        val completed =
                req.getParameter("completed")?.toBoolean() ?: return
        Scheduler.markAs(key = key, completionStatus = completed)
    }

}