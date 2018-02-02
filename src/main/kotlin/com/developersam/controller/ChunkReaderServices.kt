package com.developersam.controller

import com.developersam.chunkreader.AnalyzedArticle
import com.developersam.chunkreader.AnalyzedArticles
import com.developersam.chunkreader.ChunkReaderMainProcessor
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.summary.SummaryRequest
import com.developersam.webcore.service.HttpMethod
import com.developersam.webcore.service.NoArgService
import com.developersam.webcore.service.OneArgService
import com.developersam.webcore.service.StructuredInputService

/**
 * A chunk reader service that loads list of articles submitted by the user,
 * or gives a login URL to the user.
 */
object ChunkReaderArticleListService : NoArgService() {

    override val uri: String = "/apis/chunkreader/load"

    override val output: Any
        get() = AnalyzedArticles.asList

}

/**
 * A service that loads all the detailed information for a given article's key.
 */
object ChunkReaderArticleDetailService : OneArgService(parameterName = "key") {

    override val uri: String = "/apis/chunkreader/articleDetail"

    override fun output(argument: String): AnalyzedArticle? {
        return AnalyzedArticle.fromKey(keyString = argument)
    }

}

/**
 * A chunk reader service that loads more or less summary based on the given
 * user input for a specific article.
 */
object ChunkReaderAdjustSummaryService : StructuredInputService<SummaryRequest>(
        inType = SummaryRequest::class.java
) {

    override val uri: String = "/apis/chunkreader/adjustSummary"
    override val method: HttpMethod = HttpMethod.POST

    override fun output(input: SummaryRequest): List<String>? {
        return RetrievedSummaries.from(summaryRequest = input)?.asList
    }

}

/**
 * A chunk reader service that analyze the given article.
 */
object ChunkReaderAnalyzeArticleService : StructuredInputService<RawArticle>(
        inType = RawArticle::class.java
) {

    override val uri: String = "/apis/chunkreader/analyze"
    override val method: HttpMethod = HttpMethod.POST

    override fun output(input: RawArticle): Boolean {
        return ChunkReaderMainProcessor.process(article = input)
    }

}