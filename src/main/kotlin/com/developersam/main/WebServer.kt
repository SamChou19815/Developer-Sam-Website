@file:JvmName("WebServer")

package com.developersam.main

import com.developersam.chunkreader.AnalyzedArticle
import com.developersam.chunkreader.AnalyzedArticles
import com.developersam.chunkreader.ChunkReaderMainProcessor
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.summary.RetrievedSummaries
import com.developersam.chunkreader.summary.SummaryRequest
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerItemData
import com.developersam.game.ten.TenBoard
import com.developersam.game.ten.TenClientMove
import com.developersam.database.runBlocking
import com.developersam.util.fromBuffer
import com.developersam.util.gson
import com.developersam.util.toJsonConsumer
import com.developersam.web.auth.FirebaseAuthHandler
import com.developersam.web.auth.firebaseUser
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler

/**
 * Global Authentication Handler.
 */
private val authHandler = FirebaseAuthHandler(
        adminSDKConfig = System::class.java.getResourceAsStream(
                "/secret/firebase-adminsdk.json")
);

/**
 * Assemble together routers for app TEN.
 */
private val tenRouter: Router
    get() {
        val tenRouter = Router.router(vertx)
        // Respond to human move
        tenRouter.post("/response").blockingHandler { c ->
            val clientMove = gson.fromBuffer(
                    json = c.body, clazz = TenClientMove::class.java
            )
            val serverResponse = TenBoard.respond(clientMove = clientMove)
            c.response().end(gson.toJson(serverResponse))
        }
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
        schedulerRouter.get("/load").handler { c ->
            val user = c.user().firebaseUser
            Scheduler.getAllSchedulerItems(
                    user = user, consumer = gson.toJsonConsumer(context = c)
            )
        }
        // Write an item
        schedulerRouter.post("/write").handler { c ->
            val user = c.user().firebaseUser
            val itemData = gson.fromBuffer(
                    json = c.body, clazz = SchedulerItemData::class.java
            )
            val result = itemData.writeToDatabase(user = user).toString()
            c.response().end(result)
        }
        // Delete an item
        schedulerRouter.delete("/delete").handler { c ->
            val user = c.user().firebaseUser
            val key: String = c.request().getParam("key")
            Scheduler.delete(user = user, key = key)
            c.response().end()
        }
        // Mark as complete
        schedulerRouter.post("/markAs").handler { c ->
            val user = c.user().firebaseUser
            val request = c.request()
            val key: String? = request.getParam("key")
            val completed: Boolean? =
                    request.getParam("completed")?.toBoolean()
            if (key != null && completed != null) {
                Scheduler.markAs(user = user, key = key, completed = completed)
            }
            c.response().end()
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
        chunkReaderRouter.get("/load").handler { c ->
            AnalyzedArticles.get(
                    user = c.user().firebaseUser,
                    consumer = gson.toJsonConsumer(context = c)
            )
        }
        // Display Article Detail
        chunkReaderRouter.get("/articleDetail").handler { c ->
            val key: String? = c.request().getParam("key")
            AnalyzedArticle.fromKey(
                    keyString = key,
                    consumer = gson.toJsonConsumer(context = c)
            )
        }
        // Adjust Amount of Summary
        chunkReaderRouter.post("/adjustSummary").handler { c ->
            val request = gson.fromBuffer(
                    json = c.body, clazz = SummaryRequest::class.java
            )
            runBlocking(consumer = gson.toJsonConsumer(context = c)) {
                RetrievedSummaries.from(request)?.asList
            }
        }
        // Analyze An Item: TIME CONSUMING!
        chunkReaderRouter.post("/analyze").blockingHandler { c ->
            val article = gson.fromBuffer(
                    json = c.body, clazz = RawArticle::class.java
            )
            val result = ChunkReaderMainProcessor.process(
                    user = c.user().firebaseUser, article = article
            )
            c.response().end(result.toString())
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