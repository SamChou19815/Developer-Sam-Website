package com.developersam.controller.scheduler

import com.developersam.model.scheduler.SchedulerItemData
import com.developersam.util.gson
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet( "/apis/scheduler/write")
class WriteItemServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val data = gson.fromJson(
                req.reader, SchedulerItemData::class.java)
        resp.characterEncoding = "UTF-8"
        resp.writer.print(data.writeToDatabase())
    }

}
