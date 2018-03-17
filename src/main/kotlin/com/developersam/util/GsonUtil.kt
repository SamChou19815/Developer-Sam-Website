@file:JvmName(name = "GsonUtil")

package com.developersam.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.netty.buffer.ByteBufInputStream
import io.vertx.core.buffer.Buffer
import java.io.InputStreamReader
import java.util.Date

/**
 * Create a [GsonBuilder] of system global configuration.
 */
private fun globalGsonBuilder(): GsonBuilder {
    return GsonBuilder().registerTypeAdapter(Date::class.java, GsonDateAdapter)
}

/**
 * A default global [Gson] for the entire app.
 */
@JvmField
val gson: Gson = globalGsonBuilder().create()

/**
 * Converts a [Buffer] object from VertX to a normal Java object with type
 * specified by [clazz].
 */
fun <T> Gson.fromBuffer(json: Buffer, clazz: Class<T>): T {
    val reader = InputStreamReader(ByteBufInputStream(json.byteBuf))
    return this.fromJson(reader, clazz)
}