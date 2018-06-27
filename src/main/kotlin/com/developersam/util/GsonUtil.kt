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
 * A default global [Gson] for the entire app.
 */
@JvmField
val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Key::class.java, object : JsonDeserializer<Key>, JsonSerializer<Key> {
            override fun deserialize(e: JsonElement, t: Type, c: JsonDeserializationContext): Key =
                    Key.fromUrlSafe(e.asJsonPrimitive.asString)

            override fun serialize(k: Key, t: Type, c: JsonSerializationContext): JsonElement =
                    JsonPrimitive(k.toUrlSafe())
        })
        .create()
