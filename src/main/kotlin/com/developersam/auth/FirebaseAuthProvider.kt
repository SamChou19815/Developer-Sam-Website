package com.developersam.auth

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User

/**
 * [FirebaseAuthProvider] is responsible for authentication and interaction with
 * Vertx Auth.
 * It takes a [firebaseService] to authenticate users.
 */
internal class FirebaseAuthProvider(
        private val firebaseService: FirebaseService
) : AuthProvider {

    override fun authenticate(authInfo: JsonObject,
                              resultHandler: Handler<AsyncResult<User>>) {
        val token: String? = authInfo.getString("token")
        val user = firebaseService.getUser(idToken = token)
        val result: AsyncResult<User> = Future.succeededFuture(user)
        resultHandler.handle(result)
    }

}