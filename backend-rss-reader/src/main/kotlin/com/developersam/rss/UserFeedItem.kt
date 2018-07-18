package com.developersam.rss

import com.google.cloud.datastore.Key

/**
 * [UserFeedItem] records the information about a user's specific interaction with the feed.
 *
 * @property feedKey key of the parent feed.
 * @property key key of this item.
 * @property title title of the item.
 * @property link link of the item.
 * @property description description of the item.
 * @property publicationTime publication time of the item.
 * @property isRead whether the item is read.
 */
data class UserFeedItem(
        private val feedKey: Key,
        private val key: Key,
        private val title: String,
        private val link: String,
        private val description: String,
        private val publicationTime: Long,
        private val isRead: Boolean
) : Comparable<UserFeedItem> {

    override fun compareTo(other: UserFeedItem): Int =
            -publicationTime.compareTo(other = other.publicationTime)

}
