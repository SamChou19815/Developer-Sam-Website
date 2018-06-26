@file:JvmName(name = "WebServer")

package com.developersam.main

import com.developersam.auth.FirebaseAuthHandler
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.game.ten.Board.Companion.respond
import com.developersam.scheduler.SchedulerItem
import com.developersam.util.blockingJsonHandler
import com.developersam.util.blockingRequestHandler
import com.developersam.util.functionalHandler
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.datastore.Key
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler


/**
 * Global Authentication Handler.
 */
private val authHandler = System::class.java
        .getResourceAsStream("/secret/firebase-adminsdk.json")
        .let { GoogleCredentials.fromStream(it) }
        .let { FirebaseOptions.Builder().setCredentials(it).build() }
        .let { FirebaseApp.initializeApp(it) }
        .let { FirebaseAuthHandler(firebaseAuth = FirebaseAuth.getInstance(it)) }

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
    post("/write").blockingJsonHandler<SchedulerItem> { item, user -> item.upsert(user = user) }
    // Delete an item
    delete("/delete").blockingRequestHandler { req, user ->
        val key: String? = req.getParam("key")
        if (key != null) {
            SchedulerItem.delete(user = user, key = Key.fromUrlSafe(key))
        }
    }
    // Mark as complete
    post("/mark_as").blockingRequestHandler { req, user ->
        val key: String? = req.getParam("key")
        val completed: Boolean? = req.getParam("completed")?.toBoolean()
        if (key != null && completed != null) {
            SchedulerItem.markAs(user = user, key = Key.fromUrlSafe(key), isCompleted = completed)
        }
    }
}

/**
 * Assemble together routers for app Scheduler.
 */
private val chunkReaderRouter: Router = Router.router(vertx).apply {
    route().blockingHandler(authHandler)
    // Load Items
    get("/load").blockingRequestHandler { _, user -> Article[user] }
    // Display Article Detail
    get("/article_detail").blockingRequestHandler { req, user ->
        val key = Key.fromUrlSafe(req.getParam("key"))
        Article[user, key]
    }
    // Adjust Amount of Summary
    post("/adjust_summary").blockingRequestHandler { req, user ->
        val key = Key.fromUrlSafe(req.getParam("key"))
        val limit = req.getParam("limit").toInt()
        Summary[user, key, limit]
    }
    // Analyze An Item
    post("/analyze").blockingJsonHandler<RawArticle> { a, user -> a.process(user = user) }
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
