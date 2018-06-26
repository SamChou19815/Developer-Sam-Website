package com.developersam.main

import com.developersam.auth.FirebaseUser
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.game.ten.Board
import com.developersam.scheduler.SchedulerItem
import com.developersam.util.asJson
import com.developersam.util.toJson
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.datastore.Key
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark.delete
import spark.Spark.get
import spark.Spark.notFound
import spark.Spark.port
import spark.Spark.post
import spark.Spark.staticFileLocation
import spark.kotlin.before
import spark.kotlin.halt
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * [WebApp] is the entry point of the Spark based web server.
 */
object WebApp {

    /**
     * Global Authentication Handler.
     */
    @JvmStatic
    private val firebaseAuth: FirebaseAuth = System::class.java
            .getResourceAsStream("/secret/firebase-adminsdk.json")
            .let { GoogleCredentials.fromStream(it) }
            .let { FirebaseOptions.Builder().setCredentials(it).build() }
            .let { FirebaseApp.initializeApp(it) }
            .let { FirebaseAuth.getInstance(it) }

    /**
     * [transformer] transforms the response to correct form.
     */
    @JvmStatic
    private val transformer: ResponseTransformer = ResponseTransformer { r -> r.asJson }

    /**
     * [get] registers a GET handler with [path] and a user given [function].
     */
    @JvmStatic
    private inline fun get(
            path: String, crossinline function: Request.(resp: Response) -> Any?
    ): Unit = get(path, Route { request, response -> request.function(response) }, transformer)

    /**
     * [post] registers a POST handler with [path] and a user given [function].
     */
    @JvmStatic
    private inline fun post(
            path: String, crossinline function: Request.(resp: Response) -> Any?
    ): Unit = post(path, Route { request, response -> request.function(response) }, transformer)

    /**
     * [delete] registers a DELETE handler with [path] and a user given [function].
     */
    @JvmStatic
    private inline fun delete(
            path: String, crossinline function: Request.(resp: Response) -> Any?
    ): Unit = delete(path, Route { request, response -> request.function(response) }, transformer)

    /**
     * [Request.user] returns the [FirebaseUser] detected from the request.
     */
    @JvmStatic
    private val Request.user: FirebaseUser
        get() = attribute("user") ?: throw halt(code = 401)

    /**
     * [main] is the entry point of [WebApp].
     *
     * @param args these info will be ignored right now.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        port(8080)
        staticFileLocation("/public")
        notFound { _, resp ->
            resp.status(200)
            WebApp::class.java.getResourceAsStream("/public/index.html")
                    .let { BufferedReader(InputStreamReader(it)) }
                    .lineSequence()
                    .joinToString(separator = "\n")
        }
        before(path = "/apis/*") {
            val userOpt = request.queryParams("token")
                    ?.let { firebaseAuth.verifyIdTokenAsync(it).get() }
                    ?.let { FirebaseUser(token = it) }
            request.attribute("user", userOpt)
        }
        initializeHandlers()
    }

    /**
     * [initializeHandlers] initializes a list of handlers.
     */
    @JvmStatic
    private fun initializeHandlers() {
        // TEN
        post(path = "/apis/ten/response") { _ -> Board.respond(toJson()) }
        // Scheduler
        get(path = "/apis/scheduler/load") { _ -> SchedulerItem[user] }
        post(path = "/apis/scheduler/write") { _ ->
            toJson<SchedulerItem>().upsert(user = user) ?: halt(code = 400)
        }
        delete(path = "/apis/scheduler/delete") { _ ->
            val key: String? = queryParams("key")
            if (key != null) {
                SchedulerItem.delete(user = user, key = Key.fromUrlSafe(key))
            }
        }
        post(path = "/apis/scheduler/mark_as") { _ ->
            val key: String? = queryParams("key")
            val completed: Boolean? = queryParams("completed")?.toBoolean()
            if (key != null && completed != null) {
                SchedulerItem.markAs(
                        user = user, key = Key.fromUrlSafe(key), isCompleted = completed
                )
            }
        }
        // ChunkReader
        get(path = "/apis/chunkreader/load") { _ -> Article[user] }
        get(path = "/apis/chunkreader/article_detail") { _ ->
            val key = Key.fromUrlSafe(queryParams("key"))
            Article[user, key]
        }
        post(path = "/apis/chunkreader/adjust_summary") { _ ->
            val key = Key.fromUrlSafe(queryParams("key"))
            val limit = queryParams("limit").toInt()
            Summary[user, key, limit]
        }
        post(path = "/apis/chunkreader/analyze") { _ -> toJson<RawArticle>().process(user = user) }
    }

}
