package com.developersam.rss

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import java.net.URL

/**
 * [FeedParser] is responsible for parsing the feed.
 */
object FeedParser {

    /**
     * [parse] parses the rss feed in the given [url] and returns the parsed result, which may be
     * `null` to indicate a failure.
     */
    @JvmStatic
    fun parse(url: String): RssFeed? {
        val rawFeed: SyndFeed = try {
            SyndFeedInput().build(XmlReader(URL(url)))
        } catch (e: Exception) {
            return null
        }
        val rssFeedTitle = rawFeed.title ?: return null
        val rssFeedLink = rawFeed.link ?: return null
        val rssFeedDescription = rawFeed.description ?: return null
        val rawEntries: List<SyndEntry> = rawFeed.entries ?: return null
        return RssFeed(
                title = rssFeedTitle, link = rssFeedLink, description = rssFeedDescription,
                items = rawEntries.mapNotNull { entry ->
                    val title = entry.title ?: return@mapNotNull null
                    val link = entry.link ?: return@mapNotNull null
                    val description = entry.description?.value ?: return@mapNotNull null
                    RssFeedItem(title = title, link = link, description = description)
                }
        )
    }

}
