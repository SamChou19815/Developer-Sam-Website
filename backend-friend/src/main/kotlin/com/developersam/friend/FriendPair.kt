package com.developersam.friend

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typestore.TypedEntity
import typestore.TypedEntityCompanion
import typestore.TypedTable

/**
 * [FriendPair] represents a pair of friends in the database.
 * It has the invariant that the record a-b and b-a always both appear or none appear.
 */
object FriendPair {

    /**
     * [Table] is the table definition of [FriendPair].
     */
    private object Table : TypedTable<Table>(tableName = "FriendPair") {
        val firstUserKey = keyProperty(name = "first_user_key")
        val secondUserKey = keyProperty(name = "second_user_key")
    }

    /**
     * [PairEntity] is the entity definition of [FriendPair].
     */
    private class PairEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val secondUserKey: Key = Table.secondUserKey.delegatedValue

        companion object : TypedEntityCompanion<Table, PairEntity>(table = Table) {
            override fun create(entity: Entity): PairEntity = PairEntity(entity = entity)
        }
    }

    /**
     * [get] returns a list of friends for [user].
     */
    @JvmStatic
    internal operator fun get(user: GoogleUser): Set<GoogleUser> =
            PairEntity.query { filter { table.firstUserKey eq user.keyNotNull } }
                    .mapNotNull { entity -> GoogleUser.getByKey(key = entity.secondUserKey) }
                    .toSet()

    /**
     * [exists] reports whether the friend pair with [firstUserKey] and [secondUserKey] exists
     * in the system.
     */
    @JvmStatic
    fun exists(firstUserKey: Key, secondUserKey: Key): Boolean =
            PairEntity.any {
                filter {
                    table.firstUserKey eq firstUserKey
                    table.secondUserKey eq secondUserKey
                }
            }

    /**
     * [insert] inserts the friend pair with [firstUserKey] and [secondUserKey] into the system.
     * It will first checks whether this record already exists in the system. If so, it will
     * refuse to do so and return false. Otherwise, it will insert the record and return true.
     */
    @JvmStatic
    fun insert(firstUserKey: Key, secondUserKey: Key): Boolean {
        if (exists(firstUserKey, secondUserKey)) {
            return false
        }
        val source = if (firstUserKey == secondUserKey) {
            // Optimize for add self as friend case
            listOf(firstUserKey to secondUserKey)
        } else {
            listOf(firstUserKey to secondUserKey, secondUserKey to firstUserKey)
        }
        PairEntity.batchInsert(source = source) { (first, second) ->
            table.firstUserKey gets first
            table.secondUserKey gets second
        }
        return true
    }

    /**
     * [delete] will delete the friend pair with [firstUserKey] and [secondUserKey] from the
     * system if it exists.
     */
    @JvmStatic
    fun delete(firstUserKey: Key, secondUserKey: Key) {
        val entitiesToDelete = arrayListOf<PairEntity>()
        PairEntity.query {
            filter {
                table.firstUserKey eq firstUserKey
                table.secondUserKey eq secondUserKey
            }
        }.forEach { entitiesToDelete.add(it) }
        PairEntity.query {
            filter {
                table.firstUserKey eq secondUserKey
                table.secondUserKey eq firstUserKey
            }
        }.forEach { entitiesToDelete.add(it) }
        PairEntity.delete(entities = *entitiesToDelete.toTypedArray())
    }

}
