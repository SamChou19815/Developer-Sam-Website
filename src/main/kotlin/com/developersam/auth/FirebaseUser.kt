package com.developersam.auth

import com.google.firebase.auth.FirebaseToken
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * A [FirebaseUser] is an class that contains many information of a Firebase
 * user, obtained from a token.
 * A Firebase user can be Google, Twitter, Facebook, Github, ..., user.
 */
class FirebaseUser internal constructor(token: FirebaseToken) : AbstractUser() {

    /**
     * UID of the user.
     */
    val uid: String = token.uid
    /**
     * Name of the user.
     */
    val name: String = token.name
    /**
     * Email of the user.
     */
    val email: String = token.email
    /**
     * Picture of the user.
     */
    val picture: String = token.picture

    /**
     * The auth provider bind to the user.
     * TODO not very sure what this means
     */
    private var authProvider: AuthProvider? = null

    override fun doIsPermitted(permission: String,
                               resultHandler: Handler<AsyncResult<Boolean>>) {
        // TODO dummy implementation right now.
        resultHandler.handle(Future.succeededFuture(true))
    }

    override fun setAuthProvider(authProvider: AuthProvider?) {
        this.authProvider = authProvider
    }

    override fun principal(): JsonObject =
            json {
                obj(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "picture" to picture
                )
            }

}