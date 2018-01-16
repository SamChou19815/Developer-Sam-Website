package com.developersam.controller.chunkreader

import com.developersam.model.chunkreader.ChunkReaderMainProcessor
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
        resp.writer.print(ChunkReaderMainProcessor.process(
                text = req.reader.readText()))
    }

}