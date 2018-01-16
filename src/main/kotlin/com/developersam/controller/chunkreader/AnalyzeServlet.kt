package com.developersam.controller.chunkreader

import com.developersam.model.chunkreader.ChunkReaderMainProcessor
import com.developersam.model.chunkreader.RawArticle
import com.developersam.webcore.gson.gson
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Analyze the given text given to the chunk reader.
 */
@WebServlet("/apis/chunkreader/analyze")
class AnalyzeServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val article = gson.fromJson(req.reader, RawArticle::class.java)
        resp.writer.print(ChunkReaderMainProcessor.process(article = article))
    }

}