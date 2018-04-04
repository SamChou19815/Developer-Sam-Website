@file:JvmName(name = "VertxUtil")

package com.developersam.database

import com.developersam.main.vertx
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

/**
 * [Consumer] consumes a value.
 */
typealias Consumer<T> = (T) -> Unit

/**
 * [Producer] produces a value.
 */
typealias Producer<T> = () -> T

/**
 * [consumeBy] lets an object to be consumed by a given [consumer].
 */
fun <T> T.consumeBy(consumer: Consumer<T>) = consumer(this)

/**
 * The dummy handler to handle `null` handlers.
 */
private val dummyHandler: Handler<AsyncResult<Any>> = Handler {}

/**
 * [getHandler] creates a handler of Vert.x from the given consumer [c].
 * If [c] is empty, then the handler created is a dummy handler which directly
 * ignores the result.
 */
@Suppress(names = ["UNCHECKED_CAST"])
fun <R> getHandler(c: Consumer<R>?): Handler<AsyncResult<R>> {
    if (c == null) {
        return dummyHandler as Handler<AsyncResult<R>>
    }
    return Handler { res ->
        if (res.succeeded()) {
            res.result().consumeBy(consumer = c)
        } else if (res.failed()) {
            throw res.cause()
        }
    }
}

/**
 * [runBlocking] runs some blocking code specified by [body] and passes the
 * result in [consumer].
 */
inline fun <R> runBlocking(
        noinline consumer: Consumer<R>? = null, crossinline body: (Unit) -> R) {
    vertx.executeBlocking(Handler {
        it.complete(body(Unit))
    }, false, getHandler(consumer))
}