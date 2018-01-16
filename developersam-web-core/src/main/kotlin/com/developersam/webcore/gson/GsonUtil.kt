@file:JvmName(name = "GsonUtil")

package com.developersam.webcore.gson

import com.developersam.webcore.date.commonDateFormat
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Create a [GsonBuilder] of system global configuration.
 */
private fun globalGsonBuilder(): GsonBuilder {
    return GsonBuilder().setDateFormat(commonDateFormat)
}

/**
 * A default global [Gson] for the entire app.
 */
@JvmField
val gson: Gson = globalGsonBuilder().create()