package com.developersam.rss

import com.google.cloud.datastore.Cursor

/**
 * [CursoredUserFeed] represents a list of user RSS [items] with a [cursor] to mark the fetch
 * breakpoint.
 */
data class CursoredUserFeed(val items: List<UserFeedItem>, val cursor: Cursor)
