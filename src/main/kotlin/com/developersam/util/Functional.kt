package com.developersam.util

/**
 * [Consumer] consumes a value.
 */
typealias Consumer<T> = (T) -> Unit

/**
 * [Producer] produces a value.
 */
typealias Producer<T> = () -> T

/**
 * [consumeBy] lets an object to be consumed by a given [consumer].
 */
fun <T> T.consumeBy(consumer: Consumer<T>) = consumer(this)