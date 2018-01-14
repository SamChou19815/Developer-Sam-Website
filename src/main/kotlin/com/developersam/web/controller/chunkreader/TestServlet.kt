package com.developersam.web.controller.chunkreader

import com.developersam.web.model.chunkreader.NLPAPIAnalyzer

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Run a sentiment analysis on "Hello World".
 */
@WebServlet("/apis/chunkreader/test")
class TestServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val analyzer = NLPAPIAnalyzer.analyze("Four score and seven " +
                "years ago, I created the matrix")
        resp.writer.println(analyzer?.sentiment)
        resp.writer.println(analyzer?.entities)
        resp.writer.println(analyzer?.sentences)
        resp.writer.println(analyzer?.categories)
    }

}