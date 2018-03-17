package com.developersam.chunkreader

import com.google.cloud.datastore.Key

/**
 * [ChunkReaderSubProcessor] defines how one aspects of the text is further
 * analyzed and stored into the database.
 */
internal interface ChunkReaderSubProcessor {

    /**
     * Report the [name] of the processor.
     */
    val name: String

    /**
     * Further analyze the API result from the [NLPAPIAnalyzer] and put the
     * analysis result into the database that links together with [textKey].
     */
    fun process(analyzer: NLPAPIAnalyzer, textKey: Key)

}