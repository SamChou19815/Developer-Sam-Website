package com.developersam.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import java.io.InputStream

/**
 * The singleton object for firebase services.
 *
 * @constructor created by specifying an input stream of SDK config json file.
 */
internal class FirebaseService(adminSDKConfig: InputStream) {

    /**
     * The singleton firebase app.
     */
    private val firebaseApp: FirebaseApp

    init {
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(adminSDKConfig))
                .build()
        firebaseApp = FirebaseApp.initializeApp(options)
    }

    /**
     * The singleton firebase auth.
     */
    private val firebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    /**
     * Obtain a [FirebaseUser], which may not exist, from a user given
     * [idToken], which is allowed to be non-existent.
     */
    internal fun getUser(idToken: String?): FirebaseUser? {
        if (idToken == null) {
            return null
        }
        val token: FirebaseToken? = try {
            firebaseAuth.verifyIdTokenAsync(idToken).get()
        } catch (e: FirebaseAuthException) {
            null
        }
        return if (token == null) null else FirebaseUser(token = token)
    }

}
