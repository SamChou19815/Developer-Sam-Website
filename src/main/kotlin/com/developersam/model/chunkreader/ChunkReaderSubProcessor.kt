package com.developersam.model.chunkreader

import com.google.appengine.api.datastore.Key

/**
 * The [ChunkReaderSubProcessor] defines how one aspects of the text is further
 * analyzed and stored into the database.
 */
@FunctionalInterface
interface ChunkReaderSubProcessor {

    /**
     * Further analyze the API result from the [NLPAPIAnalyzer] and put the
     * analysis result into the database that links together with [textKey].
     */
    fun process(analyzer: NLPAPIAnalyzer, textKey: Key)

}