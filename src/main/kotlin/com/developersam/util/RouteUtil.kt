package com.developersam.util

import spark.Request
import java.io.InputStreamReader

/**
 * [asJson] converts an object to a string for a web server in expected ways.
 */
val Any?.asJson: String
    get() = when {
        this == Unit -> ""
        this is String -> this
        else -> gson.toJson(this)
    }

/**
 * [Request.toJson] converts the body of the `Request` to a parsed json object.
 */
inline fun <reified T> Request.toJson(): T =
        gson.fromJson(InputStreamReader(this.raw().inputStream), T::class.java)
