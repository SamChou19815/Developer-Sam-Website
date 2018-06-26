package com.developersam.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer
import org.pac4j.core.authorization.authorizer.RequireAnyAttributeAuthorizer
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.config.ConfigFactory
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.sparkjava.DefaultHttpActionAdapter
import org.pac4j.sparkjava.SparkWebContext
import spark.Request
import spark.Response
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A [FirebaseUser] is an class that contains many information of a Firebase user, obtained from a
 * token.
 * A Firebase user can be Google, Twitter, Facebook, Github, ..., user.
 *
 * @property uid id the user.
 * @property name name of the user.
 * @property email email of the user.
 * @property picture picture of the user.
 * @property issuer token issuer of the user.
 */
data class FirebaseUser(
        val uid: String, val name: String, val email: String,
        val picture: String, val issuer: String
) {

    /**
     * Construct from a [FirebaseToken].
     */
    private constructor(token: FirebaseToken) : this(
            uid = token.uid, name = token.name, email = token.email,
            picture = token.picture, issuer = token.issuer
    )

    /**
     * [Profile] is the user profile of the firebase user.
     *
     * @constructor created and delegated by a [FirebaseUser] [user].
     */
    private class Profile(val user: FirebaseUser) : CommonProfile() {

        override fun getId(): String = user.uid

        override fun getEmail(): String = user.email

        override fun getDisplayName(): String = user.name

        override fun getUsername(): String = user.name

        override fun getPictureUrl(): URI = URI(user.picture)

        init {
            setId(user.uid)
            isRemembered = true
        }

    }

    /**
     * [AuthConfigFactory] is used to produce auth config.
     *
     * @property firebaseAuth the auth used to authenticate users.
     * @property headerName the header name that contains the firebase token, which defaults to
     * `"Firebase-Auth-Token"`.
     * @property authorize the authorize function that gives a yes/no permission answer to a
     * request.
     */
    class AuthConfigFactory(
            private val firebaseAuth: FirebaseAuth,
            private val headerName: String = "Firebase-Auth-Token",
            private val authorize: (context: WebContext, user: FirebaseUser) -> Boolean
    ) : ConfigFactory {

        override fun build(vararg parameters: Any?): Config {
            val headerClient = HeaderClient(headerName) { credentials, _ ->
                val idToken = (credentials as? TokenCredentials)?.token ?: return@HeaderClient
                val firebaseToken = try {
                    firebaseAuth.verifyIdToken(idToken)
                } catch (e: Exception) {
                    Logger.getGlobal().log(Level.SEVERE, e) { "Auth Error" }
                    return@HeaderClient
                }
                val firebaseUser = FirebaseUser(token = firebaseToken)
                val profile = FirebaseUser.Profile(firebaseUser)
                credentials.userProfile = profile
            }
            val clients = Clients(headerClient).apply {
                val generator = AuthorizationGenerator<Profile> { context, profile ->
                    if (authorize(context, profile.user)) {
                        profile.addPermission("AUTHORIZED")
                    }
                    profile
                }
                addAuthorizationGenerator(generator)
            }
            return Config(clients).apply {
                addAuthorizer("authenticated", IsAuthenticatedAuthorizer<Profile>())
                val requiredToBeAuthorized =
                        RequireAnyAttributeAuthorizer<Profile>("AUTHORIZED")
                addAuthorizer("authorized", requiredToBeAuthorized)
                httpActionAdapter = DefaultHttpActionAdapter()
            }
        }

    }

    companion object {

        /**
         * [fromRequest] returns a [FirebaseUser] from a [request] and [response].
         */
        fun fromRequest(request: Request, response: Response): FirebaseUser? {
            val context = SparkWebContext(request, response)
            val manager = ProfileManager<Profile>(context)
            return manager.get(false).orElse(null)?.user
        }

    }

}
