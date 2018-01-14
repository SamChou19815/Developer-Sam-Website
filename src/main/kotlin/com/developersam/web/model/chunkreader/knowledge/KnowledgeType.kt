package com.developersam.web.model.chunkreader.knowledge

import com.google.cloud.language.v1beta2.Entity

/**
 * A collection of all known knowledge entity types.
 */
enum class KnowledgeType {

    PERSON, LOCATION, ORGANIZATION, EVENT, WORK_OF_ART, CONSUMER_GOOD;

    companion object {
        /**
         * Convert an [Entity.Type] to a [KnowledgeType] in the system.
         */
        fun from(entityType: Entity.Type): KnowledgeType? {
            return when (entityType) {
                Entity.Type.PERSON -> PERSON
                Entity.Type.LOCATION -> LOCATION
                Entity.Type.ORGANIZATION -> ORGANIZATION
                Entity.Type.EVENT -> EVENT
                Entity.Type.WORK_OF_ART -> WORK_OF_ART
                Entity.Type.CONSUMER_GOOD -> CONSUMER_GOOD
                else -> null
            }
        }
    }

}