package com.developersam.webcore.datastore

@FunctionalInterface
interface Writable {
    /**
     * Write the current record into the database and return a [Boolean] value
     * to indicate whether the operation is successful.
     */
    fun writeToDatabase(): Boolean
}