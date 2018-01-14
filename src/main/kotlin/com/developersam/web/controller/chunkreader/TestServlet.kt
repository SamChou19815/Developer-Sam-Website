package com.developersam.web.controller.chunkreader

import com.google.cloud.language.v1beta2.Document
import com.google.cloud.language.v1beta2.Document.Type
import com.google.cloud.language.v1beta2.LanguageServiceClient

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
        val client = LanguageServiceClient.create()
        try {
            client.use { language ->
                // The text to analyze
                val doc = Document.newBuilder()
                        .setContent("Hello, world!")
                        .setType(Type.PLAIN_TEXT)
                        .build()
                // Detects the sentiment of the text
                val sentiment = language.analyzeSentiment(doc).documentSentiment
                resp.writer.printf("%d %d",
                        sentiment.score, sentiment.magnitude)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            client.close()
        }
    }

}