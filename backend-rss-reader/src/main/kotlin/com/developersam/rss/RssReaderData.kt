package com.developersam.rss

import com.developersam.auth.GoogleUser

/**
 * [RssReaderData] is the collection of all RSS reader data load for the user.
 */
data class RssReaderData(val feed: CursoredUserFeed, val subscriptions: List<Feed>) {

    companion object {

        /**
         * Returns the [RssReaderData] for the given [user].
         */
        operator fun get(user: GoogleUser): RssReaderData =
                RssReaderData(
                        feed = UserFeedItem[user],
                        subscriptions = UserFeedSubscription[user].mapNotNull { Feed[it] }.toList()
                )

    }

}
