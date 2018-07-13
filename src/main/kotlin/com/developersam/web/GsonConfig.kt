package com.developersam.web

import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Key
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import typedstore.gson.CursorTypeAdapter
import typedstore.gson.KeyTypeAdapter

/**
 * A default global [Gson] for the entire app.
 */
internal val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Key::class.java, KeyTypeAdapter)
        .registerTypeAdapter(Cursor::class.java, CursorTypeAdapter)
        .create()
