package com.developersam.controller

import com.developersam.control.MasterServlet
import com.developersam.control.ServiceRunner
import javax.servlet.annotation.WebServlet

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

@WebServlet("/apis/*")
class MainServlet : MasterServlet() {

    override val serviceRunner: ServiceRunner = masterServiceRunner

}