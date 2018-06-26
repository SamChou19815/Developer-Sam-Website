package com.developersam.auth

import com.google.firebase.auth.FirebaseToken
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider

/**
 * A [FirebaseUser] is an class that contains many information of a Firebase user, obtained from a
 * token.
 * A Firebase user can be Google, Twitter, Facebook, Github, ..., user.
 *
 * @property uid id the user.
 * @property name name of the user.
 * @property email email of the user.
 * @property picture picture of the user.
 */
data class FirebaseUser(val uid: String, val name: String, val email: String, val picture: String) :
        AbstractUser() {

    /**
     * Construct from a [FirebaseToken].
     */
    internal constructor(token: FirebaseToken) :
            this(uid = token.uid, name = token.name, email = token.email, picture = token.picture)

    /**
     * The auth provider bind to the user.
     * TODO not very sure what this means
     */
    private var authProvider: AuthProvider? = null

    override fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>) {
        resultHandler.handle(Future.succeededFuture(true))
    }

    override fun setAuthProvider(authProvider: AuthProvider?) {
        this.authProvider = authProvider
    }

    override fun principal(): JsonObject = JsonObject().apply {
        put("uid", uid)
        put("name", name)
        put("email", email)
        put("picture", picture)
    }

}
