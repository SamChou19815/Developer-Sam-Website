package com.developersam.rss

data class RssFeed(
        val title: String, val link: String, val description: String,
        val items: List<RssFeedItem> = emptyList()
)
