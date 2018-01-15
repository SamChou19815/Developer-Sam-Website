package com.developersam.controller.scheduler

import com.developersam.model.scheduler.Scheduler
import com.google.appengine.api.users.UserServiceFactory

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.developersam.util.GsonUtil.GSON

/**
 * A servlet that loads list of scheduler items onto /apps/scheduler/.
 */
@WebServlet("/apis/scheduler/load")
class LoadItemsServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val userService = UserServiceFactory.getUserService()
        if (userService.isUserLoggedIn) {
            GSON.toJson(Scheduler.allSchedulerItems, resp.writer)
        } else {
            resp.writer.print(
                    "url: " + userService.createLoginURL("/scheduler"))
        }
    }

}