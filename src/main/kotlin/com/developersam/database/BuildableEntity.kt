package com.developersam.database

import com.google.cloud.datastore.Entity

/**
 * [BuildableEntity] defines how to transform a normal object to an entity that
 * can be inserted into the Datastore.
 */
@FunctionalInterface
interface BuildableEntity {

    /**
     * [toEntityBuilder] converts itself to a entity builder whose construction
     * is complete.
     *
     * Effect: None.
     */
    fun toEntityBuilder(): Entity.Builder

    /**
     * [toEntity] converts itself to an entity.
     *
     * Effect: None.
     */
    fun toEntity(): Entity = toEntityBuilder().build()

}