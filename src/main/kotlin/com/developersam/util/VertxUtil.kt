@file:JvmName(name = "VertxUtil")

package com.developersam.util

import com.developersam.main.vertx
import io.vertx.core.AsyncResult
import io.vertx.core.Handler

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
 * [executeBlocking] runs some blocking code specified by [body] and passes
 * the result in [consumer].
 */
inline fun <R> executeBlocking(
        noinline consumer: Consumer<R>? = null,
        crossinline body: (Unit) -> R) {
    vertx.executeBlocking(Handler {
        it.complete(body(Unit))
    }, false, getHandler(consumer))
}