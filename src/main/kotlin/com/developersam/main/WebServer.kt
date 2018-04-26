@file:JvmName("WebServer")

package com.developersam.main

import cognitivej.vision.face.scenario.FaceScenarios
import com.developersam.chunkreader.AnalyzedArticle
import com.developersam.chunkreader.AnalyzedArticles
import com.developersam.chunkreader.ChunkReaderMainProcessor
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.summary.SummaryRequest
import com.developersam.game.ten.TenBoard.Companion.respond
import com.developersam.game.ten.TenClientMove
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerItemData
import com.developersam.util.blockingJsonHandler
import com.developersam.util.blockingRequestHandler
import com.developersam.util.functionalHandler
import com.developersam.util.jsonHandler
import com.developersam.util.requestHandler
import com.developersam.util.userHandler
import com.developersam.web.auth.FirebaseAuthHandler
import com.developersam.web.firebase.FirebaseService
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler

/**
 * Globally used firebase service.
 */
private val firebaseService = FirebaseService(
        adminSDKConfig = System::class.java.getResourceAsStream("/secret/firebase-adminsdk.json"))
/**
 * Global Authentication Handler.
 */
private val authHandler = FirebaseAuthHandler(firebaseService = firebaseService)

/**
 * Assemble together routers for app TEN.
 */
private val tenRouter: Router
    get() {
        val tenRouter = Router.router(vertx)
        // Respond to human move
        tenRouter.post("/response").functionalHandler(
                c = TenClientMove::class.java, f = ::respond
        )
        return tenRouter
    }

/**
 * Assemble together routers for app Scheduler.
 */
private val schedulerRouter: Router
    get() {
        val schedulerRouter = Router.router(vertx)
        schedulerRouter.route().blockingHandler(authHandler)
        // Load items
        schedulerRouter.get("/load").userHandler { user, printer ->
            Scheduler.getAllSchedulerItems(user = user, printer = printer)
        }
        // Write an item
        schedulerRouter.post("/write").jsonHandler(
                c = SchedulerItemData::class.java) { itemData, user, printer ->
            itemData.writeToDatabase(user = user)
            printer(Unit)
        }
        // Delete an item
        schedulerRouter.delete("/delete").requestHandler { req, user, printer ->
            val key: String = req.getParam("key")
            Scheduler.delete(user = user, key = key)
            printer(Unit)
        }
        // Mark as complete
        schedulerRouter.post("/markAs").requestHandler { req, user, printer ->
            val key: String? = req.getParam("key")
            val completed: Boolean? =
                    req.getParam("completed")?.toBoolean()
            if (key != null && completed != null) {
                Scheduler.markAs(user = user, key = key, completed = completed)
            }
            printer(Unit)
        }
        return schedulerRouter
    }

/**
 * Assemble together routers for app Scheduler.
 */
private val chunkReaderRouter: Router
    get() {
        val chunkReaderRouter = Router.router(vertx)
        chunkReaderRouter.route().blockingHandler(authHandler)
        // Load Items
        chunkReaderRouter.get("/load").userHandler { user, printer ->
            AnalyzedArticles.get(user = user, printer = printer)
        }
        // Display Article Detail
        chunkReaderRouter.get("/articleDetail").blockingRequestHandler { req, _ ->
            AnalyzedArticle.fromKey(keyString = req.getParam("key"))
        }
        // Adjust Amount of Summary
        chunkReaderRouter.post("/adjustSummary").jsonHandler(
                c = SummaryRequest::class.java) { summaryRequest, _, printer ->
            RetrievedSummaries.from(summaryRequest = summaryRequest)?.printList(printer = printer)
                    ?: printer(Unit)
        }
        // Analyze An Item: TIME CONSUMING!
        chunkReaderRouter.post("/analyze").blockingJsonHandler(
                c = RawArticle::class.java) { rawArticle, user ->
            ChunkReaderMainProcessor.process(user = user, article = rawArticle)
        }
        return chunkReaderRouter
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
    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.route().handler(StaticHandler.create("public"))
    router.mountSubRouter("/apis", apiRouter)
    router.route().handler {
        if (!it.response().ended()) {
            it.response().sendFile("public/index.html")
        }
    }
    // Step 2: Start Server
    server.requestHandler { router.accept(it) }.listen(8080)
}