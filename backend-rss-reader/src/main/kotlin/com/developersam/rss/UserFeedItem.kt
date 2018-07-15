package com.developersam.rss

import com.google.cloud.datastore.Key

/**
 * [UserFeedItem] records the information about a user's specific interaction with the feed.
 */
data class UserFeedItem(
        private val feedKey: Key? = null,
        private val title: String,
        private val link: String,
        private val description: String,
        private val publicationTime: Long,
        private val isRead: Boolean
)
