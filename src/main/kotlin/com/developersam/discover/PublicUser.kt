package com.developersam.discover

import com.developersam.auth.FirebaseUser
import com.developersam.util.buildAndInsertEntity
import com.developersam.util.runQueryOf
import com.developersam.util.safeGetString
import com.developersam.util.setString
import com.developersam.util.update
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.StructuredQuery.PropertyFilter.eq

/**
 * Commonly used datastore kind.
 */
private const val kind = "PublicUser"

/**
 * [PublicUserData] is the data submitted by the user.
 */
class PublicUserData private constructor() {

    /**
     * An optional GitHub account info.
     */
    private val githubAccount: String? = null

    /**
     * [writeToDatabase] updates the corresponding public user with the new
     * given data (i.e. no missing info and the item really belongs to the
     * given [user]).
     * It returns whether the operation was successful.
     */
    fun writeToDatabase(user: FirebaseUser) {
        val userEmail = user.email
        val nickname = user.name
        val entityOpt: Entity? = runQueryOf(
                kind = kind, filter = eq("userEmail", userEmail)
        ).firstOrNull()
        val builder: (Entity.Builder) -> Unit = {
            it.apply {
                set("userEmail", userEmail)
                set("nickname", nickname)
                setString(
                        name = "githubAccount",
                        value = githubAccount?.takeIf { it.isNotBlank() }
                )
            }
        }
        entityOpt?.update(builder) ?: buildAndInsertEntity(
                kind = kind, construct = builder)
    }

}

/**
 * [PublicUser] represents a set of information of a user that is publicly
 * viewable by all the users of the system. The information contained in this
 * class is usually non-sensitive information.
 */
class PublicUser private constructor(
        @field:Transient private val entity: Entity
) : Comparable<PublicUser> {

    /**
     * Nickname of the user.
     */
    private val nickname: String = entity.getString("nickname")

    /**
     * GitHub account of the user. It's not the full URL.
     */
    private val githubAccount: String? =
            entity.safeGetString("githubAccount")

    /**
     * [hasGithub] reports whether the user has a publicly available GitHub
     * account.
     */
    private fun hasGithub(): Boolean = githubAccount != null

    override fun compareTo(other: PublicUser): Int {
        val c = hasGithub().compareTo(other = other.hasGithub())
        return if (c == 0) nickname.compareTo(other = other.nickname) else 0
    }

    companion object {
        val list: List<PublicUser>
            get() = runQueryOf(kind = kind)
                    .map(::PublicUser)
                    .filter(PublicUser::hasGithub)
                    .sorted()
                    .toList()
    }

}