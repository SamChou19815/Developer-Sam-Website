package com.developersam.controller.chunkreader

import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.summary.SummaryRequest
import com.developersam.webcore.gson.gson
import com.google.gson.reflect.TypeToken
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * A servlet that loads more or less summary based on the given user input
 * for a specific article.
 */
@WebServlet("/apis/chunkreader/adjustSummary")
class AdjustSummaryServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val summaryRequest: SummaryRequest =
                gson.fromJson(req.reader, SummaryRequest::class.java)
        val retrievedSummaries = RetrievedSummaries.from(
                summaryRequest = summaryRequest)
        if (retrievedSummaries == null) {
            gson.toJson(null, resp.writer)
            return
        }
        val type = object : TypeToken<List<String>>() {}.type
        gson.toJson(retrievedSummaries.asList, type, resp.writer)
    }

}