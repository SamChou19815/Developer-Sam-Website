@file:JvmName(name = "WebApp")

package com.developersam.main

import com.developersam.auth.FirebaseUser
import com.developersam.auth.FirebaseUser.SecurityFilters.Companion.user
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.game.ten.Board
import com.developersam.scheduler.SchedulerData
import com.developersam.scheduler.SchedulerEvent
import com.developersam.scheduler.SchedulerItem
import com.developersam.util.gson
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.datastore.Key
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.fluentd.logger.FluentLogger
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark
import spark.Spark.get
import spark.Spark.path
import spark.kotlin.before
import spark.kotlin.halt
import java.io.PrintWriter
import java.io.StringWriter
import java.util.HashMap

/*
 * ------------------------------------------------------------------------------------------
 * Part 1: Config
 * ------------------------------------------------------------------------------------------
 */

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
 * [ERRORS] is the global fluent logger.
 */
private val ERRORS: FluentLogger = FluentLogger.getLogger("myapp")

/**
 * [Role] defines a set of roles supported by the system.
 */
private enum class Role { USER, ADMIN }

/**
 * [SecurityFilters] can be used to create security filters.
 */
private object SecurityFilters : FirebaseUser.SecurityFilters<Role>(firebaseAuth, { Role.USER })

/*
 * ------------------------------------------------------------------------------------------
 * Part 2: Common Helper Functions
 * ------------------------------------------------------------------------------------------
 */

/**
 * [transformer] transforms the response to correct form.
 */
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
 * [before] registers a before security filter with [path] and a user given a required [role].
 */
private fun before(path: String, role: Role): Unit =
        Spark.before(path, SecurityFilters.withRole(role = role))

/**
 * [get] registers a GET handler with [path] and a user given function [f].
 */
private inline fun get(path: String, crossinline f: Request.(Response) -> Any?): Unit =
        Spark.get(path, Route { request, response -> request.f(response) }, transformer)

/**
 * [post] registers a POST handler with [path] and a user given function [f].
 */
private inline fun post(path: String, crossinline f: Request.(Response) -> Any?): Unit =
        Spark.post(path, Route { request, response -> request.f(response) }, transformer)

/**
 * [delete] registers a DELETE handler with [path] and a user function [f].
 */
private inline fun delete(path: String, crossinline f: Request.(Response) -> Any?): Unit =
        Spark.delete(path, Route { request, response -> request.f(response) }, transformer)

/**
 * [badRequest] is used to indicate a bad request.
 */
private fun badRequest(): Nothing = throw halt(code = 400)

/*
 * ------------------------------------------------------------------------------------------
 * Part 4: Route Declarations
 * ------------------------------------------------------------------------------------------
 */

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    get(path = "/apis/echo") { _ -> "OK" }
    path("/apis/public", ::initializePublicApiHandlers)
    path("/apis/user", ::initializeUserApiHandlers)
}

/**
 * [initializePublicApiHandlers] initializes a list of public API handlers.
 */
private fun initializePublicApiHandlers() {
    // TEN
    post(path = "/ten/response") { Board.respond(toJson()) }
}

/**
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    // Scheduler
    before(path = "/*", role = Role.USER)
    path("/scheduler") {
        get(path = "/load") { _ -> SchedulerData[user] }
        post(path = "/edit") { _ ->
            val type = queryParams("type") ?: badRequest()
            val key = when (type) {
                "item" -> toJson<SchedulerItem>().upsert(user = user)?.toUrlSafe()
                "event" -> toJson<SchedulerEvent>().upsert(user = user)?.toUrlSafe()
                else -> null
            }
            key ?: badRequest()
        }
        delete(path = "/delete") { _ ->
            val type: String? = queryParams("type")
            val key: String? = queryParams("key")
            when {
                key == null -> Unit
                type == "item" -> SchedulerItem.delete(user = user, key = Key.fromUrlSafe(key))
                type == "event" -> SchedulerEvent.delete(user = user, key = Key.fromUrlSafe(key))
                else -> Unit
            }
        }
        post(path = "/mark_item_as") { _ ->
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
        post(path = "/analyze") { _ -> toJson<RawArticle>().process(user = user) }
        get(path = "/article_detail") { _ ->
            val key = Key.fromUrlSafe(queryParams("key"))
            Article[user, key]
        }
        post(path = "/adjust_summary") { _ ->
            val key = Key.fromUrlSafe(queryParams("key"))
            val limit = queryParams("limit").toInt()
            Summary[user, key, limit]
        }
    }
}

/*
 * ------------------------------------------------------------------------------------------
 * Part 5: Main
 * ------------------------------------------------------------------------------------------
 */

/**
 * [main] is the entry point.
 *
 * @param args these info will be ignored right now.
 */
fun main(args: Array<String>) {
    Spark.port(8080)
    Spark.exception(Exception::class.java) { e, _, _ ->
        val exceptionWriter = StringWriter()
        e.printStackTrace(PrintWriter(exceptionWriter))
        val data = HashMap<String, Any>()
        data["message"] = exceptionWriter.toString()
        ERRORS.log("errors", data)
        throw e
    }
    initializeApiHandlers()
}
