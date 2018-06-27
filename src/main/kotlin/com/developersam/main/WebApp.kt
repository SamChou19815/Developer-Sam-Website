package com.developersam.main

import com.developersam.auth.FirebaseUser
import com.developersam.auth.FirebaseUser.SecurityFilters.Companion.user
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.game.ten.Board
import com.developersam.scheduler.SchedulerItem
import com.developersam.util.gson
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.datastore.Key
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.pac4j.sparkjava.SecurityFilter
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark.before
import spark.Spark.delete
import spark.Spark.get
import spark.Spark.notFound
import spark.Spark.path
import spark.Spark.port
import spark.Spark.post
import spark.Spark.staticFileLocation
import spark.kotlin.halt
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * [WebApp] is the entry point of the Spark based web server.
 */
object WebApp {

    /**
     * The global Authentication Handler.
     */
    private val firebaseAuth: FirebaseAuth = System::class.java
            .getResourceAsStream("/secret/firebase-adminsdk.json")
            .let { GoogleCredentials.fromStream(it) }
            .let { FirebaseOptions.Builder().setCredentials(it).build() }
            .let { FirebaseApp.initializeApp(it) }
            .let { FirebaseAuth.getInstance(it) }

    /**
     * [SecurityFilters] can be used to create security filters.
     */
    private object SecurityFilters : FirebaseUser.SecurityFilters(firebaseAuth = firebaseAuth)

    /**
     * [securityFilter] is the global security filter.
     */
    private val securityFilter: SecurityFilter = SecurityFilters.create { _, _ -> true }

    /**
     * [transformer] transforms the response to correct form.
     */
    @JvmStatic
    private val transformer: ResponseTransformer = ResponseTransformer { r ->
        val s = when (r) {
            Unit -> ""; is String -> r; else -> gson.toJson(r)
        }
        s
    }

    /**
     * [Request.toJson] converts the body of the `Request` to a parsed json object.
     */
    private inline fun <reified T> Request.toJson(): T = gson.fromJson(body(), T::class.java)

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
        initializeApiHandlers()
    }

    /**
     * [initializeApiHandlers] initializes a list of handlers.
     */
    @JvmStatic
    private fun initializeApiHandlers() {
        path("/apis/public", ::initializePublicApiHandlers)
        path("/apis/user", ::initializeUserApiHandlers)
    }

    /**
     * [initializePublicApiHandlers] initializes a list of public API handlers.
     */
    @JvmStatic
    private fun initializePublicApiHandlers() {
        // TEN
        post(path = "/ten/response") { _ -> Board.respond(toJson()) }
    }

    /**
     * [initializeUserApiHandlers] initializes a list of user API handlers.
     */
    @JvmStatic
    private fun initializeUserApiHandlers() {
        before("/*", securityFilter)
        // Scheduler
        path("/scheduler") {
            get(path = "/load") { _ -> SchedulerItem[user] }
            post(path = "/write") { _ ->
                toJson<SchedulerItem>().upsert(user = user)?.toUrlSafe() ?: halt(code = 400)
            }
            delete(path = "/delete") { _ ->
                val key: String? = queryParams("key")
                if (key != null) {
                    SchedulerItem.delete(user = user, key = Key.fromUrlSafe(key))
                }
            }
            post(path = "/mark_as") { _ ->
                val key: String? = queryParams("key")
                val completed: Boolean? = queryParams("completed")?.toBoolean()
                if (key != null && completed != null) {
                    SchedulerItem.markAs(user, Key.fromUrlSafe(key), completed)
                }
            }
        }
        // ChunkReader
        path("/chunkreader") {
            get(path = "/load") { _ -> Article[user] }
            get(path = "/article_detail") { _ ->
                val key = Key.fromUrlSafe(queryParams("key"))
                Article[user, key]
            }
            post(path = "/adjust_summary") { _ ->
                val key = Key.fromUrlSafe(queryParams("key"))
                val limit = queryParams("limit").toInt()
                Summary[user, key, limit]
            }
            post(path = "/analyze") { _ -> toJson<RawArticle>().process(user = user) }
        }
    }

}
