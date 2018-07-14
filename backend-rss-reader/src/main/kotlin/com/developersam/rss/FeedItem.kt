package com.developersam.rss

import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.sun.tools.doclets.internal.toolkit.util.DocPath.parent
import typedstore.TypedEntity
import typedstore.TypedEntityBuilder
import typedstore.TypedEntityCompanion
import typedstore.TypedTable

/**
 * [FeedItem] is a feed item with standard properties [title], [link], and [description].
 * It has a [feedKey] to help identify the feed it belongs to.
 * It is always a child of [Feed].
 */
data class FeedItem(
        private val feedKey: Key? = null,
        val title: String, val link: String, val description: String
) {

    /**
     * [Table] is the table definition of [FeedItem].
     */
    private object Table : TypedTable<Table>(tableName = "RssFeedItem") {
        val title = stringProperty(name = "title")
        val link = stringProperty(name = "link")
        val description = stringProperty(name = "description")
    }

    /**
     * [ItemEntity] is the table definition of [FeedItem].
     */
    private class ItemEntity(entity: Entity) : TypedEntity<Table>(entity = entity) {
        val title: String = Table.title.delegatedValue
        val link: String = Table.link.delegatedValue
        val description: String = Table.description.delegatedValue

        companion object : TypedEntityCompanion<Table, ItemEntity>(table = Table) {
            override fun create(entity: Entity): ItemEntity = ItemEntity(entity = entity)
        }

        /**
         * [asRssFeedItem] returns the entity as the [FeedItem] feed.
         */
        val asRssFeedItem: FeedItem
            get() = FeedItem(
                    feedKey = key.parent ?: error(message = "DB corrupted!"),
                    title = title, link = link, description = description
            )

    }

    internal companion object {

        /**
         * Returns the [FeedItem] given a [key].
         */
        operator fun get(key: Key): FeedItem? = ItemEntity[key]?.asRssFeedItem

        /**
         * [batchRefresh] will refresh a list of items in batch to automatically reconcile the info
         * recorded in DB.
         */
        fun batchRefresh(feedKey: Key, items: List<FeedItem>) {
            val newItems = arrayListOf<FeedItem>()
            val existingItems = arrayListOf<FeedItem>()
            val entities = arrayListOf<ItemEntity>()
            for (item in items) {
                val entity = ItemEntity.query { filter { table.link eq item.link } }.firstOrNull()
                if (entity == null) {
                    newItems.add(element = item)
                } else {
                    existingItems.add(element = item)
                    entities.add(element = entity)
                }
            }
            val builder: TypedEntityBuilder<Table, ItemEntity>.(FeedItem) -> Unit = { item ->
                table.title to item.title
                table.link to item.link
                table.description to item.description
            }
            val itemKeys = ArrayList<Key>(newItems.size + existingItems.size)
            ItemEntity.batchInsert(parent = feedKey, source = newItems, builder = builder)
                    .forEach { itemKeys.add(element = it.key) }
            ItemEntity.batchUpdate(entities = entities, source = existingItems, builder = builder)
            entities.forEach { itemKeys.add(element = it.key) }
            UserFeedSubscription.batchUserFeedRefresh(feedItemKeys = itemKeys)
        }

    }

}
