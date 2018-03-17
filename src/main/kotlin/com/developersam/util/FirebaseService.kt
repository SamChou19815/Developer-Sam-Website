package com.developersam.util

import com.developersam.auth.FirebaseUser
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import java.util.logging.Logger

/**
 * The singleton object for firebase services.
 */
internal object FirebaseService {

    /**
     * The singleton firebase app.
     */
    private val firebaseApp: FirebaseApp

    init {
        val serviceAccount = FirebaseService::class.java.getResourceAsStream(
                "dev-sam-firebase-adminsdk.json")
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
        firebaseApp = FirebaseApp.initializeApp(options)
    }

    /**
     * The singleton firebase auth.
     */
    private val firebaseAuth = FirebaseAuth.getInstance(firebaseApp)

    /**
     * [echo] logs a simple message. Mostly used to force the setup of Firebase.
     */
    fun echo() = Logger.getGlobal().info("Firebase Service Initialized!")

    /**
     * Obtain a [FirebaseUser], which may not exist, from a user given [idToken].
     */
    fun getUser(idToken: String?): FirebaseUser? {
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
