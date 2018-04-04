@file:JvmName(name = "DatastoreUtil")

package com.developersam.database

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery.CompositeFilter.and
import com.google.cloud.datastore.StructuredQuery.Filter
import java.util.Date

/**
 * These functions are responsible for packing the API from Google Cloud Client
 * Library and delivering them in a more convenient way.
 */

// Part 1: Entities

/**
 * [buildStringValue] creates a [StringValue] from a long [String].
 */
fun buildStringValue(string: String): StringValue =
        StringValue.newBuilder(string).setExcludeFromIndexes(true).build()

/**
 * [Entity.safeGetLong] obtains a long property of an entity in a null safe way.
 */
fun Entity.safeGetLong(name: String): Long? =
        takeIf { contains(name) && !isNull(name) }?.getLong(name)

/**
 * [Entity.safeGetString] obtains a string property of an entity in a null
 * safe way.
 */
fun Entity.safeGetString(name: String): String? =
        takeIf { contains(name) && !isNull(name) }?.getString(name)

/**
 * [Entity.Builder.setLong] only sets the long value if the given long is not
 * `null`.
 */
fun Entity.Builder.setLong(name: String, value: Long?): Entity.Builder =
        value?.let { this.set(name, it) } ?: this

/**
 * [Entity.Builder.setString] only sets the string value if the given String is
 * not null. Otherwise, the value is setLong to be null.
 */
fun Entity.Builder.setString(name: String, value: String?): Entity.Builder =
        if (value == null) {
            setNull(name)
        } else {
            set(name, value)
        }

/**
 * [Timestamp.toDate] converts a Google Cloud [Timestamp] object to a [Date]
 * object for convenience use.
 */
fun Timestamp.toDate(): Date = this.toSqlTimestamp()

// Part 2: Queries

/**
 * [and] acts as a syntax sugar to write more readable filters.
 */
infix fun Filter.and(filter: Filter): Filter = and(this, filter)
