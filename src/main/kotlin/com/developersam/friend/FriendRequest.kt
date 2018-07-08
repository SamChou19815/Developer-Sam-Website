package com.developersam.friend

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typestore.TypedEntity
import typestore.TypedEntityCompanion
import typestore.TypedTable

/**
 * [FriendRequest] defines a set of methods for handling adding friends.
 */
object FriendRequest {

    /**
     * [Table] is the table definition for [FriendRequest].
     */
    private object Table : TypedTable<Table>(tableName = "AddFriendRequest") {
        val requesterUserKey = keyProperty(name = "requester_user_key")
        val responderUserKey = keyProperty(name = "responder_user_key")
    }

    /**
     * [RequestEntity] is the entity definition for [FriendRequest].
     */
    private class RequestEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val requesterUserKey: Key = Table.requesterUserKey.delegatedValue

        companion object : TypedEntityCompanion<Table, RequestEntity>(table = Table) {
            override fun create(entity: Entity): RequestEntity = RequestEntity(entity = entity)
        }
    }

    /**
     * [get] returns a list of [GoogleUser] as requesters of the [responder].
     */
    @JvmStatic
    internal operator fun get(responder: GoogleUser): List<GoogleUser> {
        val responderKey = responder.keyNotNull
        return RequestEntity.query { filter = Table.responderUserKey eq responderKey }
                .mapNotNull { entity -> GoogleUser.getByKey(key = entity.requesterUserKey) }
                .toList()
    }

    /**
     * [add] adds the friend request for [requester] to [responderUserKey] to the database.
     * If the request is invalid, it will reject and return false.
     * Otherwise, it will proceed and return true.
     */
    fun add(requester: GoogleUser, responderUserKey: Key): Boolean {
        val requesterUserKey = requester.keyNotNull
        val exists = RequestEntity.any {
            filter = (Table.responderUserKey eq responderUserKey) and
                    (Table.requesterUserKey eq requesterUserKey)
        }
        if (exists) {
            return false
        }
        RequestEntity.insert { t ->
            t[Table.requesterUserKey] = requesterUserKey
            t[Table.responderUserKey] = responderUserKey
        }
        return true
    }

    /**
     * [respond] lets [responder] respond to user with [requesterUserKey]'s friend request,
     * indicated by [approved].
     * If the response is legal, it will return true and process. Otherwise, it will reject the
     * request and return false.
     */
    @JvmStatic
    fun respond(responder: GoogleUser, requesterUserKey: Key, approved: Boolean): Boolean {
        val responderUserKey = responder.keyNotNull
        val requestKey = RequestEntity.query {
            filter = (Table.responderUserKey eq responderUserKey) and
                    (Table.requesterUserKey eq requesterUserKey)
        }.firstOrNull()?.key ?: return false
        RequestEntity.delete(requestKey)
        if (approved) {
            FriendPair.insert(firstUserKey = requesterUserKey, secondUserKey = responderUserKey)
        }
        return true
    }

}
