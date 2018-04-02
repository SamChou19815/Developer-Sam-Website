@file:JvmName("AuthUtil")
package com.developersam.auth

import io.vertx.ext.auth.User

/**
 * [User.firebaseUser] is an optional property that enables developers to
 * convert a [User] to a [FirebaseUser] conveniently in a safe way.
 */
val User.firebaseUser: FirebaseUser
    get() = this as FirebaseUser