package com.developersam.controller

import com.developersam.webcore.service.MasterServlet
import com.developersam.webcore.service.ServiceRunner
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * The master service runner.
 */
@JvmField
internal val masterServiceRunner = ServiceRunner(services = arrayOf(
        // TEN
        TenResponseService,
        // Scheduler
        SchedulerLoadItemsService,
        SchedulerWriteItemService,
        SchedulerDeleteItemService,
        SchedulerMarkAsService,
        // Chunk Reader
        ChunkReaderArticleListService,
        ChunkReaderArticleDetailService,
        ChunkReaderAdjustSummaryService,
        ChunkReaderAnalyzeArticleService
))

/**
 * [MainServlet] is used to handle API calls.
 */
@WebServlet("/apis/*")
class MainServlet : MasterServlet() {

    override val serviceRunner: ServiceRunner = masterServiceRunner

}

/**
 * [RedirectServlet] is used to redirect to static pages.
 */
@WebServlet("/redirect")
class RedirectServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val origin = "https://developersam.com"
        val path: String = req.getParameter("path")
        val fullPath = origin + path
        resp.sendRedirect(fullPath)
    }

}