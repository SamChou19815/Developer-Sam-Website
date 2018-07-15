package com.developersam.rss

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [UserFeed] contains a collection of operations related to user and feed.
 */
object UserFeed {

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
     * [ItemTable] is the table definition for [Item].
     */
    private object ItemTable : TypedTable<ItemTable>(tableName = "RssUserFeedItem") {
        val userKey = keyProperty(name = "user_key")
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
     * [ItemEntity] is the entity definition for [Item].
     */
    private class ItemEntity(entity: Entity) : TypedEntity<ItemTable>(entity = entity) {
        val feedItemKey: Key = ItemTable.feedItemKey.delegatedValue
        val isRead: Boolean = ItemTable.isRead.delegatedValue
        val lastUpdatedTime: Long = ItemTable.lastUpdatedTime.delegatedValue

        companion object : TypedEntityCompanion<ItemTable, ItemEntity>(table = ItemTable) {

            override fun create(entity: Entity): ItemEntity = ItemEntity(entity = entity)

            /**
             * [entitiesToItems] converts this collection of [entities] to a list of [Item]s.
             */
            fun entitiesToItems(entities: List<ItemEntity>): List<Item> {
                val entitiesKeyList = entities.map { it.feedItemKey }
                val feedItems = FeedItem[entitiesKeyList]
                if (feedItems.size != entitiesKeyList.size) {
                    error(message = "DB corrupted")
                }
                return entities.mapIndexed { index, entity ->
                    Item(
                            item = feedItems[index], isRead = entity.isRead,
                            lastUpdatedTime = entity.lastUpdatedTime
                    )
                }
            }

        }
    }

    /*
     * --------------------------------------------------------------------------------
     * Part 3: Data Class Definition
     * --------------------------------------------------------------------------------
     */

    /**
     * [Item] records the information about a user's specific interaction with the feed.
     */
    data class Item(
            private val item: FeedItem, private val isRead: Boolean,
            private val lastUpdatedTime: Long
    )

    /**
     * [UserFeed.CursoredFeed] represents a list of user RSS [items] with a [cursor] to mark the
     * fetch breakpoint.
     */
    data class CursoredFeed(val items: List<Item>, val cursor: Cursor)


    /*
     * --------------------------------------------------------------------------------
     * Part 4: Accessors
     * --------------------------------------------------------------------------------
     */

    /**
     * [getFeed] returns an [CursoredFeed] for the given user.
     */
    @JvmStatic
    fun getFeed(user: GoogleUser, startCursor: Cursor? = null): CursoredFeed {
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
        return CursoredFeed(items = items, cursor = cursor)
    }

    /**
     * [getFeedKeys] returns a list of feed's key subscribed by the given [user].
     */
    @JvmStatic
    private fun getFeedKeys(user: GoogleUser): List<Key> =
            SubscriptionEntity.query { filter { table.userKey eq user.keyNotNull } }
                    .mapNotNull { it.feedKey }.toList()

    /**
     * [getUserData] returns the [RssReaderData] for the given [user].
     */
    @JvmStatic
    fun getUserData(user: GoogleUser): RssReaderData =
            RssReaderData(
                    feed = getFeed(user = user), subscriptions = Feed[getFeedKeys(user = user)]
            )

    /*
     * --------------------------------------------------------------------------------
     * Part 5: Updaters
     * --------------------------------------------------------------------------------
     */

    /**
     * [batchRefresh] refreshes the feed data for all users whose feed items with
     * the given [feedItemKeys] needed to be refreshed.
     */
    @JvmStatic
    internal fun batchRefresh(feedItemKeys: List<Key>) {
        SubscriptionEntity.all().map { it.userKey }.forEach { userKey ->
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
            val nowTime = System.currentTimeMillis()
            ItemEntity.batchInsert(source = newKeys) { feedItemKey ->
                table.userKey gets userKey
                table.feedItemKey gets feedItemKey
                table.isRead gets false
                table.lastUpdatedTime gets nowTime
            }
            ItemEntity.batchUpdate(entities = entities) {
                table.isRead gets false
                table.lastUpdatedTime gets nowTime
            }
        }
    }

    /**
     * [makeSubscription] makes the [user] subscribes [url].
     *
     * @return whether the subscription attempt is successful.
     */
    @JvmStatic
    fun makeSubscription(user: GoogleUser, url: String): Boolean {
        val userKey = user.keyNotNull
        val (feed, items) = FeedParser.parse(url = url) ?: return false
        val feedKey = feed.upsert()
        val exists = SubscriptionEntity.any { filter { table.feedKey eq feedKey } }
        if (exists) {
            return false
        }
        SubscriptionEntity.insert(parent = feedKey) {
            table.userKey gets userKey
            table.feedKey gets feedKey
        }
        FeedItem.batchRefresh(feedKey = feedKey, items = items)
        return true
    }

}
