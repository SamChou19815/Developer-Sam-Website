package com.developersam.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.impl.AuthHandlerImpl
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * [FirebaseAuthHandler] is used to handle auth for vertx web.
 *
 * @constructor creates itself by a global vert.x object.
 */
class FirebaseAuthHandler(vertx: Vertx) :
        AuthHandlerImpl(FirebaseAuthProvider(vertx = vertx)) {

    override fun parseCredentials(context: RoutingContext,
                                  handler: Handler<AsyncResult<JsonObject>>) {
        val token: String? = context.request().getParam("token")
        val credential = json {
            obj("token" to token)
        }
        Future.succeededFuture(credential).setHandler(handler)
    }
}