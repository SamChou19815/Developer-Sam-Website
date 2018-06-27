package com.developersam.auth

import com.google.firebase.auth.FirebaseAuth
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer
import org.pac4j.core.authorization.authorizer.ProfileAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.TokenCredentials
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.sparkjava.DefaultHttpActionAdapter
import org.pac4j.sparkjava.SecurityFilter
import org.pac4j.sparkjava.SparkWebContext
import spark.Request
import spark.Response
import spark.kotlin.halt
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
     * [SecurityFilters] can build different specialized security filters from a common
     * [firebaseAuth] to extract users.
     *
     * When using it, you should create a singleton from this class.
     */
    open class SecurityFilters(private val firebaseAuth: FirebaseAuth) {

        /**
         * [headerClient] is the specialized client for processing the firebase token from header.
         */
        private val headerClient = HeaderClient(HEADER_NAME) { credentials, _ ->
            val idToken = (credentials as? TokenCredentials)?.token ?: return@HeaderClient
            val firebaseToken = try {
                firebaseAuth.verifyIdToken(idToken)
            } catch (e: Exception) {
                Logger.getGlobal().log(Level.SEVERE, e) { "Auth Error" }
                return@HeaderClient
            }
            val firebaseUser = FirebaseUser(
                    uid = firebaseToken.uid, name = firebaseToken.name,
                    email = firebaseToken.email, picture = firebaseToken.picture,
                    issuer = firebaseToken.issuer
            )
            val profile = FirebaseUser.Profile(firebaseUser)
            credentials.userProfile = profile
        }

        /**
         * [clients] is a set of singleton clients.
         */
        private val clients = Clients(headerClient)

        /**
         * [AuthenticatedAuthorizer] is the authorizer that authorizes if the user is authenticated.
         */
        private object AuthenticatedAuthorizer : IsAuthenticatedAuthorizer<Profile>()

        /**
         * [UserAuthorizer] is the authorizer that authorizes users according to the given
         * [authorizer].
         */
        private class UserAuthorizer(
                private val authorizer: (Request, FirebaseUser) -> Boolean
        ) : ProfileAuthorizer<Profile>() {
            override fun isAuthorized(ctx: WebContext, profiles: List<Profile>): Boolean =
                    isAllAuthorized(ctx, profiles)

            override fun isProfileAuthorized(ctx: WebContext, profile: Profile): Boolean =
                    authorizer((ctx as SparkWebContext).sparkRequest, profile.user)
        }

        /**
         * [UserSecurityFilter] is the [SecurityFilter] that automatically sets the user account in
         * the attribute for later usage.
         *
         * @param config the config constructed above.
         */
        private class UserSecurityFilter(config: Config) :
                SecurityFilter(config, HEADER_CLIENT_NAME, CUSTOM_AUTHORIZER_NAME) {

            override fun handle(request: Request, response: Response) {
                super.handle(request, response)
                val context = SparkWebContext(request, response)
                val manager = ProfileManager<Profile>(context)
                manager.get(false).takeIf { it.isPresent }?.let { profileOpt ->
                    request.attribute(USER_ATTRIBUTE_NAME, profileOpt.get().user)
                }
            }

        }

        /**
         * [create] returns a newly created specialized security filter according to the authorizer.
         *
         * @param authorizer the authorize function that gives a yes/no permission answer to a
         * request.
         */
        fun create(authorizer: (Request, FirebaseUser) -> Boolean): SecurityFilter =
                Config(clients).apply {
                    addAuthorizer(AUTHENTICATED_AUTHORIZER_NAME, AuthenticatedAuthorizer)
                    addAuthorizer(CUSTOM_AUTHORIZER_NAME, UserAuthorizer(authorizer))
                    httpActionAdapter = DefaultHttpActionAdapter()
                }.let { UserSecurityFilter(config = it) }

        companion object {

            /**
             * [HEADER_NAME] is the name of header.
             */
            private const val HEADER_NAME = "Firebase-Auth-Token"

            /**
             * [HEADER_CLIENT_NAME] is the name of header client.
             */
            private const val HEADER_CLIENT_NAME = "HeaderClient"

            /**
             * [AUTHENTICATED_AUTHORIZER_NAME] is the name of the authenticated authorizer.
             */
            private const val AUTHENTICATED_AUTHORIZER_NAME = "AuthenticatedAuthorizer"

            /**
             * [CUSTOM_AUTHORIZER_NAME] is the name of the custom authorizer.
             */
            private const val CUSTOM_AUTHORIZER_NAME = "CustomAuthorizer"

            /**
             * [USER_ATTRIBUTE_NAME] is the attribute name for user.
             */
            private const val USER_ATTRIBUTE_NAME = "user"

            /**
             * [Request.user] returns the [FirebaseUser] detected from the request.
             * If a user is not found, it will throw halt with 401 error code.
             */
            val Request.user: FirebaseUser
                get() = attribute(USER_ATTRIBUTE_NAME) ?: throw halt(code = 401)

        }

    }

}
