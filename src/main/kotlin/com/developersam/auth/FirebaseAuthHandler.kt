package com.developersam.auth

import com.google.firebase.auth.FirebaseAuth
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.impl.AuthHandlerImpl

/**
 * [FirebaseAuthHandler] is used to handle auth for vertx web.
 *
 * @constructor creates itself by an firebase auth service.
 */
class FirebaseAuthHandler(firebaseAuth: FirebaseAuth) :
        AuthHandlerImpl(FirebaseAuthProvider(firebaseAuth = firebaseAuth)) {

    override fun parseCredentials(
            context: RoutingContext, handler: Handler<AsyncResult<JsonObject>>
    ) {
        val token: String? = context.request().getParam("token")
        val credential = JsonObject().apply { put("token", token) }
        Future.succeededFuture(credential).setHandler(handler)
    }

    /**
     * [FirebaseAuthProvider] is responsible for authentication and interaction with
     * Vertx Auth.
     * It takes a [firebaseAuth] to authenticate users.
     */
    private class FirebaseAuthProvider(val firebaseAuth: FirebaseAuth) : AuthProvider {

        override fun authenticate(authInfo: JsonObject, resultHandler: Handler<AsyncResult<User>>) {
            val result: AsyncResult<User> = authInfo.getString("token")
                    ?.let { idToken ->
                        try {
                            val token = firebaseAuth.verifyIdTokenAsync(idToken).get()
                            Future.succeededFuture<User>(FirebaseUser(token = token))
                        } catch (e: Throwable) {
                            Future.failedFuture<User>(e)
                        }
                    } ?: Future.failedFuture<User>("Missing Token")
            resultHandler.handle(result)
        }

    }

}
