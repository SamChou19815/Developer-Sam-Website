package com.developersam.web.control.chunkreader;

import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.Document.Type;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.cloud.language.v1beta2.Sentiment;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Run a sentiment analysis on "Hello World".
 */
@WebServlet(name = "TestServlet", value = "/apis/chunkreader/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            // The text to analyze
            String text = "Hello, world!";
            Document doc = Document.newBuilder()
                    .setContent(text).setType(Type.PLAIN_TEXT).build();
            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc)
                    .getDocumentSentiment();
            resp.getWriter().print(sentiment.getScore() + " "
                    + sentiment.getMagnitude());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}