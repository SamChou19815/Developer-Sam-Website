@file:JvmName(name = "WebApp")

package com.developersam.main

import com.developersam.auth.GoogleUser
import com.developersam.auth.Role
import com.developersam.auth.SecurityFilters
import com.developersam.auth.SecurityFilters.Companion.user
import com.developersam.friend.FriendData
import com.developersam.friend.FriendPair
import com.developersam.friend.FriendRequest
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerData
import com.developersam.scheduler.SchedulerEvent
import com.developersam.scheduler.SchedulerProject
import com.developersam.web.badRequest
import com.developersam.web.delete
import com.developersam.web.get
import com.developersam.web.initServer
import com.developersam.web.post
import com.developersam.web.queryParamsForKey
import com.developersam.web.toJson
import org.sampl.PLInterpreter
import org.sampl.exceptions.CompileTimeError
import org.sampl.exceptions.PLException
import spark.Spark.path
import spark.kotlin.halt
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

/*
 * ------------------------------------------------------------------------------------------
 * Part 0: Config
 * ------------------------------------------------------------------------------------------
 */

/**
 * [Filters] can be used to create security filters.
 */
private object Filters : SecurityFilters(adminEmails = setOf())

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
 * [initializeUserApiHandlers] initializes a list of user API handlers.
 */
private fun initializeUserApiHandlers() {
    Filters.before(path = "/*", role = Role.USER)
    path("/friends", ::initializeFriendSystemApiHandlers)
    path("/scheduler", ::initializeSchedulerApiHandlers)
}

/**
 * [initializePublicApiHandlers] initializes a list of public API handlers.
 */
private fun initializePublicApiHandlers() {
    // SAMPL
    post(path = "/sampl/interpret") {
        val code = body() ?: badRequest()
        val atomicStringValue = AtomicReference<String>()
        val evalThread = thread(start = true) {
            val callback = try {
                "Result: ${PLInterpreter.interpret(code)}"
            } catch (e: CompileTimeError) {
                val errorMessage = e.message ?: error(message = "Impossible")
                "CompileTimeError: $errorMessage"
            } catch (e: PLException) {
                "RuntimeError: ${e.m}"
            } catch (e: Throwable) {
                e.printStackTrace()
                "UNKNOWN_ERROR"
            }
            atomicStringValue.set(callback)
        }
        evalThread.join(1000)
        val callback = atomicStringValue.get() ?: kotlin.run {
            @Suppress(names = ["DEPRECATION"])
            evalThread.stop()
            "TIME_LIMIT_EXCEEDED"
        }
        callback
    }
}

/**
 * [initializeApiHandlers] initializes a list of handlers.
 */
private fun initializeApiHandlers() {
    get(path = "/") { "OK" } // Used for health check
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
 */
fun main(): Unit = initServer(initFunction = ::initializeApiHandlers)
