package com.developersam.model.chunkreader

import com.google.appengine.api.datastore.Key

/**
 * The chunk reader processor defines how one aspects of the text is further
 * analyzed and stored into the database.
 */
@FunctionalInterface
interface ChunkReaderProcessor {

    /**
     * Further analyze the API result from the [NLPAPIAnalyzer] and put the
     * analysis result into the database that links together with [textKey].
     */
    fun process(analyzer: NLPAPIAnalyzer, textKey: Key)

}