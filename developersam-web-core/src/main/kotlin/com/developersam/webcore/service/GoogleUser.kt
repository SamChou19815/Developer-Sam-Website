package com.developersam.webcore.service

import com.google.firebase.auth.FirebaseToken

/**
 * A [GoogleUser] is an class that contains many information of a Google user,
 * obtained from a token.
 */
class GoogleUser internal constructor(token: FirebaseToken) {

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

}