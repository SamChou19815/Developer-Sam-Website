package com.developersam.auth

import com.google.firebase.auth.FirebaseToken

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
data class FirebaseUser(val uid: String, val name: String, val email: String, val picture: String) {

    /**
     * Construct from a [FirebaseToken].
     */
    internal constructor(token: FirebaseToken) :
            this(uid = token.uid, name = token.name, email = token.email, picture = token.picture)

}
