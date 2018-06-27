@file:JvmName(name = "WebApp")

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
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark
import spark.Spark.path
import spark.kotlin.halt
import java.io.BufferedReader
import java.io.InputStreamReader

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
 * [SecurityFilters] can be used to create security filters.
 */
private object SecurityFilters : FirebaseUser.SecurityFilters(firebaseAuth = firebaseAuth)

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
 * [before] registers a before security filter with [path] and a user given [authorizer].
 */
private fun before(path: String, authorizer: (Request, FirebaseUser) -> Boolean): Unit =
        Spark.before(path, SecurityFilters.create(authorizer = authorizer))

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

/*
 * ------------------------------------------------------------------------------------------
 * Part 4: Route Declarations
 * ------------------------------------------------------------------------------------------
 */

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
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
    before(path = "/*") { _, _ -> true }
    path("/scheduler") {
        get(path = "/load") { SchedulerItem[user] }
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
        get(path = "/load") { Article[user] }
        post(path = "/analyze") { toJson<RawArticle>().process(user = user) }
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
    Spark.staticFileLocation("/public")
    Spark.notFound { _, resp ->
        resp.status(200)
        SecurityFilters::class.java.getResourceAsStream("/public/index.html")
                .let { BufferedReader(InputStreamReader(it)) }
                .lineSequence()
                .joinToString(separator = "\n")
    }
    initializeApiHandlers()
}
