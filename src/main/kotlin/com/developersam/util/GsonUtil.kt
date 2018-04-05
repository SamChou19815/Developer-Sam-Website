@file:JvmName(name = "GsonUtil")

package com.developersam.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
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