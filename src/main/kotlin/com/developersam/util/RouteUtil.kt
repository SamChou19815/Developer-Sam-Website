package com.developersam.util

import com.developersam.web.auth.FirebaseUser
import com.developersam.web.auth.firebaseUser
import io.netty.buffer.ByteBufInputStream
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import java.io.InputStreamReader

private typealias FunctionalHandler<T> = (T) -> Any?
private typealias BlockingJsonHandler<T> = (T, user: FirebaseUser) -> Any?
private typealias BlockingRequestHandler = (req: HttpServerRequest, user: FirebaseUser) -> Any?

/**
 * [RoutingContext.toJson] converts the body of the `RoutingContext` to a parsed json object.
 */
inline fun <reified T> RoutingContext.toJson(): T =
        gson.fromJson(InputStreamReader(ByteBufInputStream(body.byteBuf)), T::class.java)

/**
 * [Route.functionalHandler] creates a handler that lets the router handle a request with an object.
 * [f] should directly gives back the result.
 *
 * The handler is blocking.
 */
inline fun <reified T> Route.functionalHandler(crossinline f: FunctionalHandler<T>): Route =
        blockingHandler {
            val req: T = it.toJson<T>()
            val res = f(req)
            it.response().end(gson.toJson(res))
        }

/**
 * [Route.blockingHandler] creates a handler that lets the router handle a request with a known
 * [FirebaseUser] and a request body with type [c].
 * [h] should directly gives back the result.
 *
 * The handler is blocking.
 */
inline fun <reified T> Route.blockingJsonHandler(crossinline h: BlockingJsonHandler<T>): Route =
        blockingHandler {
            val res = h(it.toJson(), it.user().firebaseUser)
            it.response().end(gson.toJson(res))
        }

/**
 * [Route.blockingRequestHandler] creates a handler that lets the router handle a request with a
 * known [FirebaseUser] and a request object to extract params.
 * [h] should directly gives back the result.
 *
 * The handler is blocking.
 */
fun Route.blockingRequestHandler(h: BlockingRequestHandler): Route =
        handler {
            val res = h(it.request(), it.user().firebaseUser)
            it.response().end(gson.toJson(res))
        }
