@file:JvmName(name = "GsonUtil")

package com.developersam.webcore.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
internal val globalGson: Gson = globalGsonBuilder().create()