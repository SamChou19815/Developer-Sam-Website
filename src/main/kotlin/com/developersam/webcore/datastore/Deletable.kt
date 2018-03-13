package com.developersam.webcore.datastore

@FunctionalInterface
interface Deletable {
    /**
     * Delete itself from the database and return a [Boolean] to indicate
     * whether the delete operation is successful.
     */
    fun deleteFromDatabase(): Boolean
}