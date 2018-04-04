package com.developersam.main

import com.developersam.web.database.DatastoreClient
import io.vertx.core.Vertx

/**
 * Global Vertx.
 */
val vertx: Vertx = Vertx.vertx()

/**
 * Globally used [Database].
 */
object Database: DatastoreClient(vertx = vertx)