package com.developersam.chunkreader

import com.developersam.web.auth.FirebaseUser
import kotlin.concurrent.thread

/**
 * A raw article input from user, which contains a [title] and [content].
 */
data class RawArticle(val title: String = "", val content: String = "") {

    /**
     * [process] will starts to process the given [RawArticle] and returns immediately whether the
     * article is well-formatted.
     */
    fun process(user: FirebaseUser): Boolean {
        if (title.isBlank() || content.isBlank()) {
            return false
        }
        thread(start = true) { Article.Processor.process(user = user, article = this) }
        return true
    }

}
