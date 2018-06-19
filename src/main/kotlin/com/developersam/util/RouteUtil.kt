package com.developersam.util

import com.developersam.web.auth.FirebaseUser
import com.developersam.web.auth.firebaseUser
import com.developersam.web.database.Consumer
import io.netty.buffer.ByteBufInputStream
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import java.io.InputStreamReader

/*
 * Some typealias to make the code more readable.
 */
private typealias Printer = Consumer<Any?>

private typealias UserHandler = (user: FirebaseUser, printer: Printer) -> Unit
private typealias JsonHandler<T> = (T, user: FirebaseUser, printer: Printer) -> Unit
private typealias RequestHandler =
        (req: HttpServerRequest, user: FirebaseUser, printer: Printer) -> Unit

private typealias FunctionalHandler<T> = (T) -> Any?
private typealias BlockingJsonHandler<T> = (T, user: FirebaseUser) -> Any?
private typealias BlockingRequestHandler = (req: HttpServerRequest, user: FirebaseUser) -> Any?

/**
 * [RoutingContext.toJson] converts the body of the `RoutingContext` to a parsed json object, with
 * type specified by [clazz].
 */
private fun <T> RoutingContext.toJson(clazz: Class<T>): T =
        gson.fromJson(InputStreamReader(ByteBufInputStream(body.byteBuf)), clazz)

/**
 * [RoutingContext.printer] creates a printer that can be used to print structured data to the
 * response stream.
 */
private val RoutingContext.printer: Printer
    get() = {
        if (it === Unit) {
            response().end()
        } else {
            response().end(gson.toJson(it))
        }
    }

/**
 * [Route.functionalHandler] creates a handler that lets the router handle a request with an object
 * of type [c].
 * [f] should directly gives back the result.
 *
 * The handler is blocking.
 */
fun <T> Route.functionalHandler(c: Class<T>, f: FunctionalHandler<T>): Route =
        blockingHandler {
            val req = it.toJson(clazz = c)
            val res = f(req)
            it.response().end(gson.toJson(res))
        }

/**
 * [Route.userHandler] creates a handler that lets the router handle a request with a known
 * [FirebaseUser] and a printer to print data to response stream.
 *
 * The handler should be non-blocking.
 */
fun Route.userHandler(h: UserHandler): Route = handler { h(it.user().firebaseUser, it.printer) }

/**
 * [Route.jsonHandler] creates a handler that lets the router handle a request with a known
 * [FirebaseUser], a request body with type [c] and a printer to print data to response stream.
 *
 * The handler should be non-blocking.
 */
fun <T> Route.jsonHandler(c: Class<T>, h: JsonHandler<T>): Route =
        handler { h(it.toJson(clazz = c), it.user().firebaseUser, it.printer) }

/**
 * [Route.blockingHandler] creates a handler that lets the router handle a request with a known
 * [FirebaseUser] and a request body with type [c].
 * [h] should directly gives back the result.
 *
 * The handler is blocking.
 */
fun <T> Route.blockingJsonHandler(c: Class<T>, h: BlockingJsonHandler<T>): Route =
        blockingHandler {
            val res = h(it.toJson(clazz = c), it.user().firebaseUser)
            it.response().end(gson.toJson(res))
        }

/**
 * [Route.requestHandler] creates a handler that lets the router handle a request with a known
 * [FirebaseUser], a request object to extract params and a printer to print data to response
 * stream.
 *
 * The handler should be non-blocking.
 */
fun Route.requestHandler(h: RequestHandler): Route =
        handler { h(it.request(), it.user().firebaseUser, it.printer) }

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