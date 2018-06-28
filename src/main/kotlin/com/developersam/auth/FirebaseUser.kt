package com.developersam.auth

import com.google.firebase.auth.FirebaseAuth
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer
import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.context.DefaultAuthorizers.CSRF
import org.pac4j.core.context.DefaultAuthorizers.SECURITYHEADERS
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
     * Best practice: when using it, you should create a singleton from this class.
     *
     * @param R the enum type of user role.
     */
    open class SecurityFilters<R : Enum<R>>(
            private val firebaseAuth: FirebaseAuth,
            private val roleAssigner: (FirebaseUser) -> R
    ) {

        /**
         * [authorizationGenerator] is the generator used to assign roles to users.
         */
        private val authorizationGenerator = AuthorizationGenerator<CommonProfile> { _, p ->
            p.apply { addRole(roleAssigner((p as Profile).user).name) }
        }

        /**
         * [headerClient] is the specialized client for processing the firebase token from header.
         */
        private val headerClient: HeaderClient = HeaderClient(HEADER_NAME) { credentials, _ ->
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
        }.apply { addAuthorizationGenerator(authorizationGenerator) }

        /**
         * [clients] is a set of singleton clients.
         */
        private val clients = Clients(headerClient)

        /**
         * [UserSecurityFilter] is the [SecurityFilter] that automatically sets the user account in
         * the attribute for later usage.
         *
         * @param config the config constructed above.
         */
        private class UserSecurityFilter(config: Config) :
                SecurityFilter(config, HEADER_CLIENT_NAME, AUTHORIZER_NAMES) {

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
         * [withRole] returns a new security filter that requires the user to have certain [role].
         */
        fun withRole(role: R): SecurityFilter =
                Config(clients).apply {
                    addAuthorizer(ROLE_AUTHORIZER_NAME,
                            RequireAllRolesAuthorizer<Profile>(role.name))
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
             * [ROLE_AUTHORIZER_NAME] is the name of the authorizer that checks roles.
             */
            private const val ROLE_AUTHORIZER_NAME = "RoleAuthorizer"

            /**
             * [AUTHORIZER_NAMES] are the names of the used authorizers.
             */
            private const val AUTHORIZER_NAMES = "$ROLE_AUTHORIZER_NAME,$SECURITYHEADERS"

            /**
             * [USER_ATTRIBUTE_NAME] is the attribute name for user.
             */
            private const val USER_ATTRIBUTE_NAME = "FirebaseUser"

            /**
             * [Request.user] returns the [FirebaseUser] detected from the request.
             * If a user is not found, it will throw halt with 401 error code.
             */
            val Request.user: FirebaseUser
                get() = attribute(USER_ATTRIBUTE_NAME) ?: throw halt(code = 401)

        }

    }

}
