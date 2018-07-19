@file:JvmName(name = "WebApp")

package com.developersam.main

import com.developersam.auth.GoogleUser
import com.developersam.auth.Role
import com.developersam.auth.SecurityFilters.Companion.user
import com.developersam.chunkreader.Article
import com.developersam.chunkreader.RawArticle
import com.developersam.chunkreader.Summary
import com.developersam.friend.FriendData
import com.developersam.friend.FriendPair
import com.developersam.friend.FriendRequest
import com.developersam.game.ten.Board
import com.developersam.rss.Feed
import com.developersam.rss.UserData
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerData
import com.developersam.scheduler.SchedulerEvent
import com.developersam.scheduler.SchedulerProject
import com.developersam.web.Filters
import com.developersam.web.badRequest
import com.developersam.web.delete
import com.developersam.web.get
import com.developersam.web.post
import com.developersam.web.queryParamsForCursor
import com.developersam.web.queryParamsForKey
import com.developersam.web.toJson
import spark.Spark
import spark.Spark.path
import spark.kotlin.halt
import kotlin.system.measureTimeMillis

/*
 * ------------------------------------------------------------------------------------------
 * Part 1: Route Declarations
 * ------------------------------------------------------------------------------------------
 */

/**
 * [initializeFriendSystemApiHandlers] initializes a list of friend system API handlers.
 */
private fun initializeFriendSystemApiHandlers() {
    get(path = "/load") { FriendData(user = user) }
    get(path = "/get_user_info") {
        val email = queryParams("email") ?: badRequest()
        GoogleUser.getByEmail(email = email)
    }
    post(path = "/add_friend_request") {
        val key = queryParamsForKey("responder_user_key")
        val successful = FriendRequest.add(requester = user, responderUserKey = key)
        if (!successful) {
            badRequest()
        }
    }
    post(path = "/respond_friend_request") {
        val key = queryParamsForKey("requester_user_key")
        val approved = queryParams("approved")?.let { it == "true" } ?: badRequest()
        val successful = FriendRequest.respond(
                responder = user, requesterUserKey = key, approved = approved
        )
        if (!successful) {
            badRequest()
        }
    }
    delete(path = "/remove_friend") {
        val friendKey = queryParamsForKey("removed_friend_key")
        FriendPair.delete(firstUserKey = user.keyNotNull, secondUserKey = friendKey)
    }
}

/**
 * [initializeSchedulerApiHandlers] initializes a list of Scheduler API handlers.
 */
private fun initializeSchedulerApiHandlers() {
    get(path = "/load") { SchedulerData(user = user) }
    path("/edit") {
        post(path = "/project") {
            toJson<SchedulerProject>().upsert(user = user)?.toUrlSafe() ?: badRequest()
        }
        post(path = "/event") {
            toJson<SchedulerEvent>().upsert(user = user)?.toUrlSafe() ?: badRequest()
        }
    }
    path("/delete") {
        delete(path = "/project") {
            val key = queryParamsForKey("key")
            SchedulerProject.delete(user = user, key = key)
        }
        delete(path = "/event") {
            val key = queryParamsForKey("key")
            SchedulerEvent.delete(user = user, key = key)
        }
    }
    post(path = "/mark_project_as") {
        val key = queryParamsForKey("key")
        val completed = queryParams("completed")?.toBoolean() ?: badRequest()
        SchedulerProject.markAs(user = user, key = key, isCompleted = completed)
    }
    get(path = "/personal_auto_schedule") {
        Scheduler(config1 = SchedulerData(user = user)).schedule()
    }
    get(path = "/friend_auto_schedule") {
        val friendKey = queryParamsForKey(name = "friend_key")
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
 * [initializeRssReaderApiHandlers] initializes a list of RSS Reader API handlers.
 */
private fun initializeRssReaderApiHandlers() {
    get(path = "/load") { UserData.getRssReaderData(user = user) }
    get(path = "/load_more_feed") {
        val cursor = queryParamsForCursor(name = "cursor")
        UserData.UserFeed[user, cursor]
    }
    post(path = "/subscribe") {
        val url = queryParams("url") ?: badRequest()
        val user = user
        if (UserData.Subscriptions.subscribe(user = user, url = url)) {
            UserData.getRssReaderData(user = user)
        } else null
    }
    post(path = "/unsubscribe") {
        val key = queryParamsForKey("key")
        UserData.Subscriptions.unsubscribe(user = user, feedKey = key)
        UserData.getRssReaderData(user = user)
    }
    post(path = "/mark_as") {
        val key = queryParamsForKey("key")
        val isRead = queryParams("is_read")?.toBoolean() ?: badRequest()
        UserData.UserFeed.markAs(user = user, userFeedItemKey = key, isRead = isRead)
    }
    post(path = "/mark_all_as") {
        val isRead = queryParams("is_read")?.toBoolean() ?: badRequest()
        UserData.UserFeed.markAllAs(user = user, isRead = isRead)
    }
    post(path = "/star") {
        val key = queryParamsForKey("key")
        UserData.UserFeed.star(user = user, userFeedItemKey = key)
    }
    post(path = "/unstar") {
        val key = queryParamsForKey("key")
        UserData.UserFeed.unstar(user = user, userFeedItemKey = key)
    }
}

/**
 * [initializeChunkReaderApiHandlers] initializes a list of Chunk Reader API handlers.
 */
private fun initializeChunkReaderApiHandlers() {
    get(path = "/load") { Article[user] }
    post(path = "/analyze") { toJson<RawArticle>().process(user = user) }
    get(path = "/adjust_summary") {
        val key = queryParamsForKey(name = "key")
        val limit = queryParams("limit")?.toInt() ?: badRequest()
        Summary[user, key, limit]
    }
    delete(path = "/delete") {
        val key = queryParamsForKey(name = "key")
        Article.delete(user = user, key = key)
    }
}

/**
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    Filters.before(path = "/*", role = Role.USER)
    path("/friends", ::initializeFriendSystemApiHandlers)
    path("/scheduler", ::initializeSchedulerApiHandlers)
    path("/rss_reader", ::initializeRssReaderApiHandlers)
    path("/chunk_reader", ::initializeChunkReaderApiHandlers)
}

/**
 * [initializePublicApiHandlers] initializes a list of public API handlers.
 */
private fun initializePublicApiHandlers() {
    // RSS Reader Cron Job
    get(path = "/rss_reader/cron") { Feed.refresh() }
    // TEN
    post(path = "/ten/response") { Board.respond(toJson()) }
}

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    get(path = "/apis/echo") { "OK" } // Used for health check
    path("/apis/public", ::initializePublicApiHandlers)
    path("/apis/user", ::initializeUserApiHandlers)
}

/*
 * ------------------------------------------------------------------------------------------
 * Part 2: Main
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
