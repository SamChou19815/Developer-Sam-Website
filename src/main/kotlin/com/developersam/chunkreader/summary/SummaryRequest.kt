package com.developersam.chunkreader.summary

/**
 * Represent a request for a summary for an article of a given [keyString] and a [limit], which will
 * be 5 if no value is given.
 */
data class SummaryRequest(val keyString: String? = null, val limit: Int = 5)