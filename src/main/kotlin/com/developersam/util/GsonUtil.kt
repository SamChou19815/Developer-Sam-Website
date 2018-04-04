@file:JvmName(name = "GsonUtil")

package com.developersam.util

import com.developersam.web.database.Consumer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.netty.buffer.ByteBufInputStream
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.Date

/**
 * The date adapter consistently used in the app.
 */
private object GsonDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    override fun serialize(src: Date, typeOfSrc: Type,
                           context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateToString(date = src));
    }

    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): Date? {
        return stringToDate(date = json.asString)
    }

}

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
 * [Gson.fromBuffer] converts a [Buffer] object from VertX to a normal Java
 * object with type specified by [clazz].
 */
fun <T> Gson.fromBuffer(json: Buffer, clazz: Class<T>): T {
    val reader = InputStreamReader(ByteBufInputStream(json.byteBuf))
    return this.fromJson(reader, clazz)
}

/**
 * [Gson.toJsonConsumer] creates a consumer that prints the result of the
 * consumer as JSON string to the client.
 */
fun <R> Gson.toJsonConsumer(context: RoutingContext): Consumer<R> = { res ->
    context.response().end(this.toJson(res))
}
