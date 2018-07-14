package com.developersam.rss

/**
 * [RssReaderData] is the collection of all RSS reader data load for the user.
 */
data class RssReaderData(val feed: UserFeed.CursoredFeed, val subscriptions: List<Feed>)
