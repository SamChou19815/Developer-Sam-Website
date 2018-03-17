package com.developersam.chunkreader.knowledge

import com.google.cloud.language.v1beta2.Entity

/**
 * A collection of all known knowledge entity types.
 */
enum class KnowledgeType {

    PERSON, LOCATION, ORGANIZATION, EVENT, WORK_OF_ART, CONSUMER_GOOD, UNKNOWN;

    companion object {
        /**
         * Convert an [Entity.Type] to a [KnowledgeType] in the system.
         */
        fun from(entityType: Entity.Type): KnowledgeType {
            return when (entityType) {
                PERSON -> PERSON
                LOCATION -> LOCATION
                ORGANIZATION -> ORGANIZATION
                EVENT -> EVENT
                WORK_OF_ART -> WORK_OF_ART
                CONSUMER_GOOD -> CONSUMER_GOOD
                else -> UNKNOWN
            }
        }
    }

}