@file:JvmName(name = "WebApp")

package com.developersam.main

import com.developersam.auth.GoogleUser
import com.developersam.auth.SecurityFilters
import com.developersam.auth.SecurityFilters.Companion.user
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.friend.FriendData
import com.developersam.friend.FriendPair
import com.developersam.friend.FriendRequest
import com.developersam.game.ten.Board
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerData
import com.developersam.scheduler.SchedulerEvent
import com.developersam.scheduler.SchedulerProject
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
import kotlin.system.measureTimeMillis

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
 * [Filters] can be used to create security filters.
 */
private object Filters : SecurityFilters<Role>(firebaseAuth, { Role.USER })

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
 * [Request.queryParamsForKeyOpt] returns the key from the given query params in this [Request].
 * If the data with [name] is not a key, it will return null.
 */
private fun Request.queryParamsForKeyOpt(name: String): Key? =
        queryParams(name)?.let { Key.fromUrlSafe(it) }

/**
 * [Request.queryParamsForKey] returns the key from the given query params in this [Request].
 * If the data with [name] is not a key, it will end with a 400 error.
 */
private fun Request.queryParamsForKey(name: String): Key =
        queryParamsForKeyOpt(name = name) ?: badRequest()

/**
 * [before] registers a before security filter with [path] and a user given a required [role].
 */
private fun before(path: String, role: Role): Unit =
        Spark.before(path, Filters.withRole(role = role))

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
 * [initializeFriendSystemApiHandlers] initializes a list of friend system API handlers.
 */
private fun initializeFriendSystemApiHandlers() {
    get(path = "/load") { _ -> FriendData(user = user) }
    get(path = "/get_user_info") { _ ->
        val email = queryParams("email") ?: badRequest()
        GoogleUser.getByEmail(email = email)
    }
    post(path = "/add_friend_request") { _ ->
        val key = queryParamsForKey("responder_user_key")
        val successful = FriendRequest.add(requester = user, responderUserKey = key)
        if (!successful) {
            badRequest()
        }
    }
    post("/respond_friend_request") { _ ->
        val key = queryParamsForKey("requester_user_key")
        val approved = queryParams("approved")?.let { it == "true" } ?: badRequest()
        val successful = FriendRequest.respond(
                responder = user, requesterUserKey = key, approved = approved
        )
        if (!successful) {
            badRequest()
        }
    }
    delete(path = "/remove_friend") { _ ->
        val friendKey = queryParamsForKey("removed_friend_key")
        FriendPair.delete(firstUserKey = user.keyNotNull, secondUserKey = friendKey)
    }
}

/**
 * [initializeSchedulerApiHandlers] initializes a list of Scheduler API handlers.
 */
private fun initializeSchedulerApiHandlers() {
    get(path = "/load") { _ -> SchedulerData(user = user) }
    post(path = "/edit") { _ ->
        val type = queryParams("type") ?: badRequest()
        val key = when (type) {
            "project" -> toJson<SchedulerProject>().upsert(user = user)?.toUrlSafe()
            "event" -> toJson<SchedulerEvent>().upsert(user = user)?.toUrlSafe()
            else -> null
        }
        key ?: badRequest()
    }
    delete(path = "/delete") { _ ->
        val type = queryParams("type") ?: badRequest()
        val key = queryParamsForKey("key")
        when (type) {
            "project" -> SchedulerProject.delete(user = user, key = key)
            "event" -> SchedulerEvent.delete(user = user, key = key)
        }
    }
    post(path = "/mark_project_as") { _ ->
        val key = queryParamsForKey("key")
        val completed = queryParams("completed")?.toBoolean() ?: badRequest()
        SchedulerProject.markAs(user = user, key = key, isCompleted = completed)
    }
    get(path = "/auto_schedule") { _ ->
        val friendKey = queryParamsForKeyOpt(name = "friend_key")
                ?: return@get Scheduler(config1 = SchedulerData(user = user)).schedule()
        val myKey = user.keyNotNull
        if (!FriendPair.exists(firstUserKey = myKey, secondUserKey = friendKey)) {
            throw halt(code = 403)
        }
        val friend = GoogleUser.getByKey(key = friendKey) ?: badRequest()
        val myConfig = SchedulerData(user = user)
        val friendConfig = SchedulerData(user = friend)
        Scheduler(config1 = myConfig, config2 = friendConfig).schedule()
    }
}

/**
 * [initializeChunkReaderApiHandlers] initializes a list of Chunk Reader API handlers.
 */
private fun initializeChunkReaderApiHandlers() {
    get(path = "/load") { _ -> Article[user] }
    post(path = "/analyze") { _ -> toJson<RawArticle>().process(user = user) }
    get(path = "/article_detail") { _ ->
        val key = queryParamsForKey(name = "key")
        Article[user, key]
    }
    get(path = "/adjust_summary") { _ ->
        val key = queryParamsForKey(name = "key")
        val limit = queryParams("limit")?.toInt() ?: badRequest()
        Summary[user, key, limit]
    }
    delete(path = "/delete") { _ ->
        val key = queryParamsForKey(name = "key")
        Article.delete(user = user, key = key)
    }
}

/**
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    before(path = "/*", role = Role.USER)
    path("/friends", ::initializeFriendSystemApiHandlers)
    path("/scheduler", ::initializeSchedulerApiHandlers)
    path("/chunkreader", ::initializeChunkReaderApiHandlers)
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
    val initTime = measureTimeMillis {
        Spark.port(8080)
        /*
        Spark.exception(Exception::class.java) { e, _, _ ->
            val exceptionWriter = StringWriter()
            e.printStackTrace(PrintWriter(exceptionWriter))
            val data = HashMap<String, Any>()
            data["message"] = exceptionWriter.toString()
            ERRORS.log("errors", data)
            throw e
        }
        */
        initializeApiHandlers()
    }
    println("Initialized in ${initTime}ms.")
}
