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
data class UserData(val feed: UserFeed, val subscriptions: List<Feed>) {

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
        val userKey: Key = ItemTable.userKey.delegatedValue
        val feedItemKey: Key = ItemTable.feedItemKey.delegatedValue
        val isRead: Boolean = ItemTable.isRead.delegatedValue

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
                    feedItems[index].toUserFeedItem(key = entity.key, isRead = entity.isRead)
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
         * [userKey] whose [feedItems] with common parent [feedKey] needed to be refreshed.
         */
        @JvmStatic
        private fun batchRefreshUserFeedItems(
                userKey: Key, feedKey: Key, feedItems: List<FeedItem>
        ) {
            // Find new keys to insert and old entities to update.
            val newItems = arrayListOf<FeedItem>()
            val existingItems = arrayListOf<FeedItem>()
            val entities = arrayListOf<ItemEntity>()
            for (item in feedItems) {
                val entityOpt = ItemEntity.query {
                    filter {
                        table.userKey eq userKey
                        table.feedItemKey eq item.feedItemKeyNotNull
                    }
                }.firstOrNull()
                if (entityOpt == null) {
                    newItems.add(element = item)
                } else {
                    existingItems.add(element = item)
                    entities.add(element = entityOpt)
                }
            }
            // Insert and update
            ItemEntity.batchInsert(source = newItems) { feedItem ->
                table.userKey gets userKey
                table.feedKey gets feedKey
                table.feedItemKey gets feedItem.feedItemKeyNotNull
                table.isRead gets false
                table.lastUpdatedTime gets feedItem.publicationTime
            }
            ItemEntity.batchUpdate(entities = entities, source = existingItems) { feedItem ->
                table.isRead gets false
                table.lastUpdatedTime gets feedItem.publicationTime
            }
        }

        /**
         * [batchRefresh] refreshes the feed subscription data for all users whose [feedItems] with
         * common parent [feedKey] needed to be refreshed.
         */
        @JvmStatic
        internal fun batchRefresh(feedKey: Key, feedItems: List<FeedItem>) {
            // Find all users and update for each user.
            SubscriptionEntity.all().map { it.userKey }.forEach { userKey ->
                batchRefreshUserFeedItems(
                        userKey = userKey, feedKey = feedKey, feedItems = feedItems
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
     * [UserFeed] represents a list of user RSS [items] with a [cursor] to mark the fetch
     * breakpoint.
     */
    data class UserFeed(val items: List<UserFeedItem>, val cursor: Cursor) {

        /**
         * [limit] is the limit of fetching. The field is used to give information to the frontend.
         */
        private val limit: Int = Constants.FETCH_LIMIT

        companion object {

            /**
             * [get] returns an [UserFeed] for the given user.
             */
            operator fun get(user: GoogleUser, startCursor: Cursor? = null): UserFeed {
                val (entities, cursor) = ItemEntity.queryCursored {
                    filter { table.userKey eq user.keyNotNull }
                    order { table.lastUpdatedTime.desc() }
                    withLimit(limit = Constants.FETCH_LIMIT)
                    startCursor?.let { startAt(cursor = it) }
                }
                val items = ItemEntity.entitiesToItems(entities = entities)
                return UserFeed(items = items, cursor = cursor)
            }

            /**
             * [markAs] marks a user feed item with [userFeedItemKey] belonging to [user] as
             * [isRead]. If the [user] does not own the item, this operation has no effect.
             */
            fun markAs(user: GoogleUser, userFeedItemKey: Key, isRead: Boolean) {
                ItemEntity[userFeedItemKey]?.takeIf { it.userKey == user.keyNotNull }?.let { e ->
                    ItemEntity.update(entity = e) { table.isRead gets isRead }
                }
            }

            /**
             * [markAllAs] marks all user feed item belongs to [user] as [isRead].
             */
            fun markAllAs(user: GoogleUser, isRead: Boolean) {
                val userKey = user.keyNotNull
                val entities = ItemEntity.query { filter { table.userKey eq userKey } }.toList()
                ItemEntity.batchUpdate(entities = entities) { table.isRead gets isRead }
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
                        feed = UserFeed[user],
                        subscriptions = Feed[getFeedKeys(user = user)]
                )

    }

}
