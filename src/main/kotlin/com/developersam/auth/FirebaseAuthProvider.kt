package com.developersam.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User

/**
 * [FirebaseAuthProvider] is responsible for authentication and interaction with
 * Vertx Auth.
 * It takes an [vertx] to handle async requests.
 */
class FirebaseAuthProvider(private val vertx: Vertx) : AuthProvider {

    override fun authenticate(authInfo: JsonObject,
                              resultHandler: Handler<AsyncResult<User>>) {
        val token: String? = authInfo.getString("token")
        vertx.executeBlocking(Handler {
            val user = FirebaseService.getUser(idToken = token)
            it.complete(user)
        }, resultHandler)
    }

}