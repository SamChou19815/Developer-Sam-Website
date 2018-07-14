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
 * [UserFeedItem] records the information about a user's specific interaction with the feed.
 */
data class UserFeedItem(
        private val item: FeedItem,
        private val isRead: Boolean,
        private val lastUpdatedTime: LocalDateTime
) {

    /**
     * [Table] is the table definition for [UserFeedItem].
     */
    private object Table : TypedTable<Table>(tableName = "RssUserFeedItem") {
        val userKey = keyProperty(name = "user_key")
        val feedItemKey = keyProperty(name = "feed_item_key")
        val isRead = boolProperty(name = "is_read")
        val lastUpdatedTime = datetimeProperty(name = "last_updated_time")
    }

    /**
     * [ItemEntity] is the entity definition for [UserFeedItem].
     */
    private class ItemEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val feedItemKey: Key = Table.feedItemKey.delegatedValue
        val isRead: Boolean = Table.isRead.delegatedValue
        val lastUpdatedTime: LocalDateTime = Table.lastUpdatedTime.delegatedValue

        companion object : TypedEntityCompanion<Table, ItemEntity>(table = Table) {
            override fun create(entity: Entity): ItemEntity = ItemEntity(entity = entity)
        }

        val asRssUserFeedItem: UserFeedItem
            get() = UserFeedItem(
                    item = FeedItem[feedItemKey] ?: error(message = "DB corrupted!"),
                    isRead = isRead, lastUpdatedTime = lastUpdatedTime
            )
    }

    companion object {

        /**
         * [get] returns an [CursoredUserFeed] for the given user.
         */
        operator fun get(user: GoogleUser, startCursor: Cursor? = null): CursoredUserFeed {
            val (sequence, newStartCursor) = ItemEntity.queryCursored {
                filter { table.userKey eq user.keyNotNull }
                order {
                    table.isRead.asc()
                    table.lastUpdatedTime.desc()
                }
                withLimit(limit = Constants.FETCH_LIMIT)
                startCursor?.let { startAt(cursor = it) }
            }
            return CursoredUserFeed(
                    items = sequence.map { it.asRssUserFeedItem }.toList(),
                    cursor = newStartCursor
            )
        }

        /**
         * [batchUserFeedRefresh] refreshes the feed data for the given user's [userKey] whose feed
         * items with the given [feedItemKeys] needed to be refreshed.
         */
        internal fun batchUserFeedRefresh(userKey: Key, feedItemKeys: List<Key>) {
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

}
