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
import com.google.cloud.datastore.Key
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark
import spark.Spark.get
import spark.Spark.path
import spark.kotlin.before
import spark.kotlin.halt
import java.lang.reflect.Type
import kotlin.system.measureTimeMillis

/*
 * ------------------------------------------------------------------------------------------
 * Part 1: Config
 * ------------------------------------------------------------------------------------------
 */

/**
 * [Role] defines a set of roles supported by the system.
 */
private enum class Role { USER, ADMIN }

/**
 * [Filters] can be used to create security filters.
 */
private object Filters : SecurityFilters<Role>(roleAssigner = { Role.USER })

/**
 * [KeyTypeAdapter] is the type adapter for [Key].
 */
private object KeyTypeAdapter : JsonDeserializer<Key>, JsonSerializer<Key> {
    override fun deserialize(e: JsonElement, t: Type, c: JsonDeserializationContext): Key =
            Key.fromUrlSafe(e.asJsonPrimitive.asString)

    override fun serialize(k: Key, t: Type, c: JsonSerializationContext): JsonElement =
            JsonPrimitive(k.toUrlSafe())
}

/**
 * A default global [Gson] for the entire app.
 */
private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Key::class.java, KeyTypeAdapter)
        .create()

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
    path("/edit") {
        post(path = "/project") { _ ->
            toJson<SchedulerProject>().upsert(user = user)?.toUrlSafe() ?: badRequest()
        }
        post(path = "/event") { _ ->
            toJson<SchedulerEvent>().upsert(user = user)?.toUrlSafe() ?: badRequest()
        }
    }
    path("/delete") {
        delete(path = "/project") { _ ->
            val key = queryParamsForKey("key")
            SchedulerProject.delete(user = user, key = key)
        }
        delete(path = "/event") { _ ->
            val key = queryParamsForKey("key")
            SchedulerEvent.delete(user = user, key = key)
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
        initializeApiHandlers()
    }
    println("Initialized in ${initTime}ms.")
}
