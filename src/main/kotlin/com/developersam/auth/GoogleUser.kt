package com.developersam.auth

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typestore.TypedEntity
import typestore.TypedEntityCompanion
import typestore.TypedTable

/**
 * A [GoogleUser] is an class that contains many information of a Google user, obtained from a
 * Firebase token.
 *
 * @property key key of the user.
 * @property uid id the user.
 * @property name name of the user.
 * @property email email of the user.
 * @property picture picture of the user.
 */
data class GoogleUser(
        val key: Key? = null, @field:Transient val uid: String,
        val name: String, val email: String, val picture: String
) {

    /**
     * [keyNotNull] returns a [Key] that is definitely not null.
     */
    val keyNotNull: Key get() = key ?: getByUid(uid = uid)?.key ?: error(message = "Impossible")

    /**
     * [upsert] is used to update the record in the database, either by inserting or updating the
     * existing data.
     *
     * @return the corresponding user with key.
     */
    fun upsert(): GoogleUser {
        val entityOpt = getEntityByUid(uid = uid)
        return UserEntity.upsert(entity = entityOpt) { t ->
            t[Table.name] = name
            t[Table.email] = email
            t[Table.picture] = picture
        }.asGoogleUser
    }

    /**
     * [Table] is the table definition for [GoogleUser]
     */
    private object Table : TypedTable<Table>(tableName = "FirebaseUser") {
        val uid = stringProperty(name = "uid")
        val name = stringProperty(name = "name")
        val email = stringProperty(name = "email")
        val picture = stringProperty(name = "picture")
    }

    /**
     * [UserEntity] is the entity definition for [GoogleUser].
     */
    private class UserEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val uid: String = Table.uid.delegatedValue
        val name: String = Table.name.delegatedValue
        val email: String = Table.email.delegatedValue
        val picture: String = Table.picture.delegatedValue

        val asGoogleUser: GoogleUser
            get() = GoogleUser(uid = uid, name = name, email = email, picture = picture)

        companion object : TypedEntityCompanion<Table, UserEntity>(table = Table) {
            override fun create(entity: Entity): UserEntity = UserEntity(entity = entity)
        }
    }

    companion object {

        /**
         * [getEntityByUid] returns a [UserEntity] by [uid], which may be `null`.
         */
        private fun getEntityByUid(uid: String): UserEntity? =
                UserEntity.query { filter = Table.uid eq uid }.firstOrNull()

        /**
         * [getByKey] returns a [GoogleUser] by the given [key], which may be `null`.
         */
        fun getByKey(key: Key): GoogleUser? = UserEntity[key]?.asGoogleUser

        /**
         * [getByUid] returns a [GoogleUser] by [uid], which may be `null`.
         */
        fun getByUid(uid: String): GoogleUser? = getEntityByUid(uid = uid)?.asGoogleUser

        /**
         * [getByEmail] returns a [GoogleUser] by [email], which may be `null`.
         */
        fun getByEmail(email: String): GoogleUser? =
                UserEntity.query { filter = Table.email eq email }.firstOrNull()?.asGoogleUser

    }

}
