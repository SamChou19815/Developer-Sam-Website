package com.developersam.util

import com.google.cloud.datastore.Key
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * [JsonPrimitive.asIntOpt] returns an int or `null` associated with this primitive.
 */
val JsonPrimitive.asIntOpt: Int? get() = if (isNumber) asInt else null

/**
 * [JsonPrimitive.asLongOpt] returns a long or `null` associated with this primitive.
 */
val JsonPrimitive.asLongOpt: Long? get() = if (isNumber) asLong else null

/**
 * [JsonPrimitive.asFloatOpt] returns a float or `null` associated with this primitive.
 */
val JsonPrimitive.asFloatOpt: Float? get() = if (isNumber) asFloat else null

/**
 * [JsonPrimitive.asDoubleOpt] returns a double or `null` associated with this primitive.
 */
val JsonPrimitive.asDoubleOpt: Double? get() = if (isNumber) asDouble else null

/**
 * [JsonPrimitive.asBooleanOpt] returns a boolean or `null` associated with this primitive.
 */
val JsonPrimitive.asBooleanOpt: Boolean? get() = if (isBoolean) asBoolean else null

/**
 * [JsonPrimitive.asStringOpt] returns a string or `null` associated with this primitive.
 */
val JsonPrimitive.asStringOpt: String? get() = if (isString) asString else null

/**
 * A default global [Gson] for the entire app.
 */
@JvmField
val gson: Gson = GsonBuilder().apply {
    registerTypeAdapter(Key::class.java, object : JsonDeserializer<Key>, JsonSerializer<Key> {
        override fun deserialize(
                json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
        ): Key = Key.fromUrlSafe(json.asJsonPrimitive.asString)

        override fun serialize(
                src: Key, typeOfSrc: Type, context: JsonSerializationContext
        ): JsonElement = JsonPrimitive(src.toUrlSafe())
    })
}.create()
