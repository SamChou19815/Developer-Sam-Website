package com.developersam.chunkreader.type

/**
 * The enum class defines a set of all supported and known types of the texts.
 * The analysis is based on text sentiment.
 * The [description] can be obtained by [toString] method.
 */
internal enum class TextType(private val description: String) {

    CONCEPT(description = "Concept illustration"),
    MIXED(description = "Mixed reaction"),
    SLIGHT_OPPOSITION(description = "Slight opposition to an opinion"),
    STRONG_OPPOSITION(description = "Strong opposition to an opinion"),
    SLIGHT_SUPPORT(description = "Slight support for an opinion"),
    STRONG_SUPPORT(description = "Strong support for an opinion");

    override fun toString(): String {
        return description
    }

}