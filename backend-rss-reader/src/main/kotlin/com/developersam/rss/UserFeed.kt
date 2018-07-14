package com.developersam.rss

import com.developersam.auth.GoogleUser
import com.google.cloud.datastore.Cursor
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import typedstore.TypedEntity
import typedstore.TypedEntityCompanion
import typedstore.TypedTable
import typedstore.nowInUTC
import java.time.LocalDateTime

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
        val lastUpdatedTime = datetimeProperty(name = "last_updated_time")
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
        val lastUpdatedTime: LocalDateTime = ItemTable.lastUpdatedTime.delegatedValue

        companion object : TypedEntityCompanion<ItemTable, ItemEntity>(table = ItemTable) {

            override fun create(entity: Entity): ItemEntity = ItemEntity(entity = entity)

            /**
             * [entitiesToItems] converts this collection of [entities] to a list of [Item]s.
             */
            fun entitiesToItems(entities: Sequence<ItemEntity>): List<Item> {
                val entitiesKeyList = entities.map { it.feedItemKey }.toList()
                val feedItems = FeedItem[entitiesKeyList]
                if (feedItems.size != entitiesKeyList.size) {
                    error(message = "DB corrupted")
                }
                return entities.mapIndexed { index, entity ->
                    Item(
                            item = feedItems[index], isRead = entity.isRead,
                            lastUpdatedTime = entity.lastUpdatedTime
                    )
                }.toList()
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
            private val lastUpdatedTime: LocalDateTime
    )

    /**
     * [UserFeed.CursoredFeed] represents a list of user RSS [items] with a [cursor] to mark the
     * fetch breakpoint.
     */
    data class CursoredFeed(val items: List<Item>, val cursor: Cursor)

    /**
     * [UserData] is the collection of all RSS reader data load for the user.
     */
    data class UserData(val feed: UserFeed.CursoredFeed, val subscriptions: List<Feed>)

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
        return CursoredFeed(items = ItemEntity.entitiesToItems(sequence), cursor = cursor)
    }

    /**
     * [getFeedKeys] returns a list of feed's key subscribed by the given [user].
     */
    @JvmStatic
    private fun getFeedKeys(user: GoogleUser): List<Key> =
            SubscriptionEntity.query { filter { table.userKey eq user.keyNotNull } }
                    .mapNotNull { it.feedKey }.toList()

    /**
     * [getUserData] returns the [UserData] for the given [user].
     */
    @JvmStatic
    fun getUserData(user: GoogleUser): UserData =
            UserData(
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
            val entities = feedItemKeys.mapNotNull { key ->
                ItemEntity.query {
                    filter {
                        table.userKey eq userKey
                        table.feedItemKey eq key
                    }
                }.firstOrNull()
            }
            val nowTime = nowInUTC()
            ItemEntity.batchUpdate(entities = entities) {
                table.isRead gets false
                table.lastUpdatedTime gets nowTime
            }
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
