package com.developersam.rss

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [UserData] contains a collection of operations related to user and feed.
 */
data class UserData(val feed: CursoredUserFeed, val subscriptions: List<Feed>) {

    /*
     * --------------------------------------------------------------------------------
     * Part 1: Table Definition
     * --------------------------------------------------------------------------------
     */

    /**
     * [SubscriptionTable] is the table definition for user's subscription.
     */
    private object SubscriptionTable : TypedTable<SubscriptionTable>(
            tableName = "RssUserFeedSubscription"
    ) {
        val userKey = keyProperty(name = "user_key")
        val feedKey = keyProperty(name = "feed_key")
    }

    /**
     * [ItemTable] is the table definition for [UserFeedItem].
     */
    private object ItemTable : TypedTable<ItemTable>(tableName = "RssUserFeedItem") {
        val userKey = keyProperty(name = "user_key")
        val feedKey = keyProperty(name = "feed_key")
        val feedItemKey = keyProperty(name = "feed_item_key")
        val isRead = boolProperty(name = "is_read")
        val lastUpdatedTime = longProperty(name = "last_updated_time")
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 2: Entity Definition
     * --------------------------------------------------------------------------------
     */

    /**
     * [SubscriptionEntity] is the entity definition for user's subscription.
     */
    private class SubscriptionEntity(entity: Entity) : TypedEntity<SubscriptionTable>(entity) {
        val userKey: Key = SubscriptionTable.userKey.delegatedValue
        val feedKey: Key = SubscriptionTable.feedKey.delegatedValue

        companion object : TypedEntityCompanion<SubscriptionTable, SubscriptionEntity>(
                table = SubscriptionTable
        ) {
            override fun create(entity: Entity): SubscriptionEntity = SubscriptionEntity(entity)
        }
    }

    /**
     * [ItemEntity] is the entity definition for [UserFeedItem].
     */
    private class ItemEntity(entity: Entity) : TypedEntity<ItemTable>(entity = entity) {
        val feedItemKey: Key = ItemTable.feedItemKey.delegatedValue
        val isRead: Boolean = ItemTable.isRead.delegatedValue
        val lastUpdatedTime: Long = ItemTable.lastUpdatedTime.delegatedValue

        companion object : TypedEntityCompanion<ItemTable, ItemEntity>(table = ItemTable) {

            override fun create(entity: Entity): ItemEntity = ItemEntity(entity = entity)

            /**
             * [entitiesToItems] converts this collection of [entities] to a list of [UserFeedItem].
             */
            fun entitiesToItems(entities: List<ItemEntity>): List<UserFeedItem> {
                val entitiesKeyList = entities.map { it.feedItemKey }
                val feedItems = FeedItem[entitiesKeyList]
                if (feedItems.size != entitiesKeyList.size) {
                    error(message = "DB corrupted")
                }
                return entities.mapIndexed { index, entity ->
                    feedItems[index].toUserFeedItem(
                            isRead = entity.isRead, lastUpdatedTime = entity.lastUpdatedTime
                    )
                }
            }

        }
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 3: Subscription Updaters
     * --------------------------------------------------------------------------------
     */

    /**
     * [Subscriptions] is a collection of subscriptions related methods.
     */
    object Subscriptions {

        /**
         * [batchRefreshUserFeedItems] refreshes the feed subscription data for the user with given
         * [userKey] whose feed items with the given [feedItemKeys] with common parent [feedKey]
         * needed to be refreshed.
         */
        @JvmStatic
        private fun batchRefreshUserFeedItems(userKey: Key, feedKey: Key, feedItemKeys: List<Key>) {
            // Find new keys to insert and old entities to update.
            val newKeys = arrayListOf<Key>()
            val entities = feedItemKeys.mapNotNull { key ->
                val keyOpt = ItemEntity.query {
                    filter {
                        table.userKey eq userKey
                        table.feedItemKey eq key
                    }
                }.firstOrNull()
                if (keyOpt == null) {
                    newKeys.add(element = key)
                }
                keyOpt
            }
            // Insert and update
            val nowTime = System.currentTimeMillis()
            ItemEntity.batchInsert(source = newKeys) { feedItemKey ->
                table.userKey gets userKey
                table.feedKey gets feedKey
                table.feedItemKey gets feedItemKey
                table.isRead gets false
                table.lastUpdatedTime gets nowTime
            }
            ItemEntity.batchUpdate(entities = entities) {
                table.isRead gets false
                table.lastUpdatedTime gets nowTime
            }
        }

        /**
         * [batchRefresh] refreshes the feed subscription data for all users whose feed items with
         * the given [feedItemKeys] with common parent [feedKey] needed to be refreshed.
         */
        @JvmStatic
        internal fun batchRefresh(feedKey: Key, feedItemKeys: List<Key>) {
            // Find all users and update for each user.
            SubscriptionEntity.all().map { it.userKey }.forEach { userKey ->
                batchRefreshUserFeedItems(
                        userKey = userKey, feedKey = feedKey, feedItemKeys = feedItemKeys
                )
            }
        }

        /**
         * [subscribe] makes the [user] subscribes [url].
         *
         * @return whether the subscription attempt is successful.
         */
        @JvmStatic
        fun subscribe(user: GoogleUser, url: String): Boolean {
            val userKey = user.keyNotNull
            // Parse and record feed metadata
            val (feed, items) = FeedParser.parse(url = url) ?: return false
            val feedKey = feed.upsert()
            // Reject subscribe to existing ones.
            val exists = SubscriptionEntity.any { filter { table.feedKey eq feedKey } }
            if (exists) {
                return false
            }
            // Record Data
            SubscriptionEntity.insert(parent = feedKey) {
                table.userKey gets userKey
                table.feedKey gets feedKey
            }
            FeedItem.batchRefresh(feedKey = feedKey, items = items)
            return true
        }

        /**
         * [unsubscribe] makes the user unsubscribe the feed with given [feedKey].
         */
        @JvmStatic
        fun unsubscribe(user: GoogleUser, feedKey: Key) {
            val userKey = user.keyNotNull
            // Delete bindings in subscription table.
            SubscriptionEntity.query {
                filter {
                    table.userKey eq userKey
                    table.feedKey eq feedKey
                }
            }.map { it.key }.toList().let { SubscriptionEntity.delete(keys = it) }
            // Delete items in user item table
            ItemEntity.query {
                filter {
                    table.userKey eq userKey
                    table.feedKey eq feedKey
                }
            }.map { it.key }.toList().let { SubscriptionEntity.delete(keys = it) }
        }

    }

    /*
     * --------------------------------------------------------------------------------
     * Part 4: Feed Accessors
     * --------------------------------------------------------------------------------
     */

    /**
     * [CursoredUserFeed] represents a list of user RSS [items] with a [cursor] to mark the
     * fetch breakpoint.
     */
    data class CursoredUserFeed(val items: List<UserFeedItem>, val cursor: Cursor) {

        companion object {

            /**
             * [get] returns an [CursoredUserFeed] for the given user.
             */
            operator fun get(user: GoogleUser, startCursor: Cursor? = null): CursoredUserFeed {
                val (sequence, cursor) = ItemEntity.queryCursored {
                    filter { table.userKey eq user.keyNotNull }
                    order {
                        table.isRead.asc()
                        table.lastUpdatedTime.desc()
                    }
                    withLimit(limit = Constants.FETCH_LIMIT)
                    startCursor?.let { startAt(cursor = it) }
                }
                val items = ItemEntity.entitiesToItems(entities = sequence.toList())
                return CursoredUserFeed(items = items, cursor = cursor)
            }

        }

    }

    /*
     * --------------------------------------------------------------------------------
     * Part 5: Data Accessors
     * --------------------------------------------------------------------------------
     */

    companion object {

        /**
         * [getFeedKeys] returns a list of feed's key subscribed by the given [user].
         */
        private fun getFeedKeys(user: GoogleUser): List<Key> =
                SubscriptionEntity.query { filter { table.userKey eq user.keyNotNull } }
                        .mapNotNull { it.feedKey }.toList()

        /**
         * [getRssReaderData] returns the [UserData] for the given [user].
         */
        fun getRssReaderData(user: GoogleUser): UserData =
                UserData(
                        feed = CursoredUserFeed[user],
                        subscriptions = Feed[getFeedKeys(user = user)]
                )

    }

}
