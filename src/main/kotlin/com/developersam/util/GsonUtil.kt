package com.developersam.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * All Gson related operation helper class.
 */
object GsonUtil {

    /**
     * A default gson from Google as a reference.
     */
    @JvmField val DEFAULT_GSON: Gson = Gson()
    /**
     * A default gson for the app.
     */
    @JvmField val GSON: Gson = defaultBuild().create()

    /**
     * Create a `GsonBuilder` of default configuration.
     *
     * @return a default `GsonBuilder`.
     */
    private fun defaultBuild(): GsonBuilder {
        return GsonBuilder().setDateFormat(DateUtil.DATE_FORMAT)
    }

}
