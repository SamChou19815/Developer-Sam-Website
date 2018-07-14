package com.developersam.web

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Key
import spark.Request
import spark.Response
import spark.ResponseTransformer
import spark.Route
import spark.Spark
import spark.Spark.get
import spark.kotlin.halt

/**
 * [Transformer] transforms the response to correct form.
 */
private object Transformer : ResponseTransformer {
    override fun render(model: Any?): String = when (model) {
        Unit -> ""; is String -> model; else -> gson.toJson(model)
    }
}

/**
 * [Request.toJson] converts the body of the `Request` to a parsed json object.
 */
internal inline fun <reified T> Request.toJson(): T = gson.fromJson(body(), T::class.java)

/**
 * [Request.queryParamsForKeyOpt] returns the key from the given query params in this [Request].
 * If the data with [name] is not a key, it will return null.
 */
internal fun Request.queryParamsForKeyOpt(name: String): Key? =
        queryParams(name)?.let(block = Key::fromUrlSafe)

/**
 * [Request.queryParamsForKey] returns the key from the given query params in this [Request].
 * If the data with [name] is not a key, it will end with a 400 error.
 */
internal fun Request.queryParamsForKey(name: String): Key =
        queryParamsForKeyOpt(name = name) ?: badRequest()

/**
 * [Request.queryParamsForCursor] returns the cursor from the given query params in this [Request].
 * If the data with [name] is not a cursor, it will end with a 400 error.
 */
internal fun Request.queryParamsForCursor(name: String): Cursor =
        queryParams(name)?.let(block = Cursor::fromUrlSafe) ?: badRequest()

/**
 * [get] registers a GET handler with [path] and a user given function [f].
 */
internal inline fun get(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.get(path, Route { request, _ -> request.f() }, Transformer)

/**
 * [post] registers a POST handler with [path] and a user given function [f].
 */
internal inline fun post(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.post(path, Route { request, _ -> request.f() }, Transformer)

/**
 * [delete] registers a DELETE handler with [path] and a user function [f].
 */
internal inline fun delete(path: String, crossinline f: Request.() -> Any?): Unit =
        Spark.delete(path, Route { request, _ -> request.f() }, Transformer)

/**
 * [badRequest] is used to indicate a bad request.
 */
internal fun badRequest(): Nothing = throw halt(code = 400)
