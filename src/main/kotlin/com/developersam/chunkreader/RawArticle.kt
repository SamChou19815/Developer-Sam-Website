package com.developersam.chunkreader

/**
 * A raw article input from user, which contains a [title] and [content].
 */
data class RawArticle(val title: String = "", val content: String = "") {

    /**
     * [isValid] returns whether the given article is valid.
     */
    val isValid: Boolean = title.isNotBlank() && content.isNotBlank()

}
