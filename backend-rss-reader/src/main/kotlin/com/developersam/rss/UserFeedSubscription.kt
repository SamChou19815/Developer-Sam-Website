package com.developersam.rss

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [UserFeedSubscription] defines a set of methods for manipulating RSS user subscription related
 * methods.
 */
object UserFeedSubscription {

    /**
     * [Table] is the table definition for [UserFeedSubscription].
     */
    private object Table : TypedTable<Table>(tableName = "RssUserFeedSubscription") {
        val userKey = keyProperty(name = "user_key")
        val feedKey = keyProperty(name = "feed_key")
    }

    /**
     * [SubscriptionEntity] is the entity definition for [UserFeedSubscription].
     */
    private class SubscriptionEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val userKey: Key = Table.userKey.delegatedValue
        val feedKey: Key = Table.feedKey.delegatedValue

        companion object : TypedEntityCompanion<Table, SubscriptionEntity>(table = Table) {
            override fun create(entity: Entity): SubscriptionEntity = SubscriptionEntity(entity)
        }
    }

    /**
     * [get] returns a list of feed's key subscribed by the given [user].
     */
    @JvmStatic
    internal operator fun get(user: GoogleUser): Sequence<Key> =
            SubscriptionEntity.query { filter { table.userKey eq user.keyNotNull } }
                    .mapNotNull { it.feedKey }

    /**
     * [batchUserFeedRefresh] refreshes the feed data for all users whose feed items with
     * the given [feedItemKeys] needed to be refreshed.
     */
    @JvmStatic
    internal fun batchUserFeedRefresh(feedItemKeys: List<Key>) {
        SubscriptionEntity.all().map { it.userKey }.forEach { userKey ->
            UserFeedItem.batchUserFeedRefresh(userKey = userKey, feedItemKeys = feedItemKeys)
        }
    }

    /**
     * [makeSubscription] makes the [user] subscribes [url].
     *
     * It will return the feed if it succeeds, or `null` if it fails.
     */
    @JvmStatic
    fun makeSubscription(user: GoogleUser, url: String): Feed? {
        val userKey = user.keyNotNull
        val (feed, items) = FeedParser.parse(url = url) ?: return null
        val feedKey = feed.key!!
        val exists = SubscriptionEntity.any { filter { table.feedKey eq feedKey } }
        if (exists) {
            return feed
        }
        SubscriptionEntity.insert(parent = feedKey) {
            table.userKey gets userKey
            table.feedKey gets feedKey
        }
        FeedItem.batchRefresh(feedKey = feedKey, items = items)
        return feed
    }

}
