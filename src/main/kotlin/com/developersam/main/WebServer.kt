@file:JvmName("WebServer")

package com.developersam.main

import com.developersam.chunkreader.AnalyzedArticle
import com.developersam.chunkreader.AnalyzedArticles
import com.developersam.chunkreader.ChunkReaderProcessor
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.summary.SummaryRequest
import com.developersam.game.ten.Board.Companion.respond
import com.developersam.scheduler.SchedulerItem
import com.developersam.util.blockingJsonHandler
import com.developersam.util.blockingRequestHandler
import com.developersam.util.functionalHandler
import com.developersam.util.jsonHandler
import com.developersam.util.requestHandler
import com.developersam.util.userHandler
import com.developersam.web.auth.FirebaseAuthHandler
import com.developersam.web.firebase.FirebaseService
import com.google.protobuf.util.JsonFormat.printer
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler

/**
 * Globally used firebase service.
 */
private val firebaseService = FirebaseService(
        adminSDKConfig = System::class.java.getResourceAsStream(
                "/secret/firebase-adminsdk.json"
        )
)
/**
 * Global Authentication Handler.
 */
private val authHandler = FirebaseAuthHandler(firebaseService = firebaseService)

/**
 * Assemble together routers for app TEN.
 */
private val tenRouter: Router = Router.router(vertx).apply {
    post("/response").functionalHandler(f = ::respond)
}

/**
 * Assemble together routers for app Scheduler.
 */
private val schedulerRouter: Router = Router.router(vertx).apply {
    route().blockingHandler(authHandler)
    // Load items
    get("/load").blockingRequestHandler { _, user -> SchedulerItem[user] }
    // Write an item
    post("/write").jsonHandler<SchedulerItem> { item, user, printer ->
        printer(item.upsert(user = user)?.toUrlSafe())
    }
    // Delete an item
    delete("/delete").blockingRequestHandler { req, user ->
        val key: String = req.getParam("key")
        SchedulerItem.delete(user = user, key = key)
    }
    // Mark as complete
    post("/mark_as").blockingRequestHandler { req, user ->
        val key: String? = req.getParam("key")
        val completed: Boolean? = req.getParam("completed")?.toBoolean()
        if (key != null && completed != null) {
            SchedulerItem.markAs(user = user, key = key, isCompleted = completed)
        }
    }
}

/**
 * Assemble together routers for app Scheduler.
 */
private val chunkReaderRouter: Router = Router.router(vertx).apply {
    route().blockingHandler(authHandler)
    // Load Items
    get("/load").blockingRequestHandler { _, user -> AnalyzedArticle[user] }
    // Display Article Detail
    get("/articleDetail").blockingRequestHandler { req, _ ->
        AnalyzedArticle.fromKey(key = req.getParam("key"))
    }
    // Adjust Amount of Summary
    post("/adjustSummary").jsonHandler<SummaryRequest> { r, _, p ->
        RetrievedSummaries.from(summaryRequest = r)?.printList(printer = p) ?: p(Unit)
    }
    // Analyze An Item: TIME CONSUMING!
    post("/analyze").blockingJsonHandler<RawArticle> { article, user ->
        ChunkReaderProcessor.process(user = user, article = article)
    }
}

/**
 * Assemble together API routers from various REST APIs.
 */
private val apiRouter: Router = Router.router(vertx).apply {
    mountSubRouter("/ten", tenRouter)
    mountSubRouter("/scheduler", schedulerRouter)
    mountSubRouter("/chunkreader", chunkReaderRouter)
}

/**
 * Entry point of the server.
 */
fun main(args: Array<String>) {
    // Step 1: Setup Vertx
    val server = vertx.createHttpServer()
    val router = Router.router(vertx).apply {
        route().handler(BodyHandler.create())
        route().handler(StaticHandler.create("public"))
        mountSubRouter("/apis", apiRouter)
        route().handler {
            if (!it.response().ended()) {
                it.response().sendFile("public/index.html")
            }
        }
    }
    // Step 2: Start Server
    server.requestHandler { router.accept(it) }.listen(8080)
}
