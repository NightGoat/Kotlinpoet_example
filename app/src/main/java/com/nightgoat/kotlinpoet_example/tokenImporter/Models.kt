package com.nightgoat.kotlinpoet_example.tokenImporter

import kotlinx.serialization.Serializable

/**
 * Document class for our json to be parsed to
 */
@Serializable(with = TokenSerializer::class)
data class Document(
    val values: List<Token<Any>>
)

/**
 * Key-Value entity for JSON with type.
 */
@Serializable
data class Token<T : Any>(
    val name: String,
    val value: T,
    val type: TokenType
)

/**
 * Type of the token in json file. Lets assume that we need only this types for now.
 */
enum class TokenType(vararg val key: String) {
    Colors("color"),
    Fonts("font"),
    Sizes("size");

    infix fun contains(key: String): Boolean {
        return this.key.any { key.contains(it, true) }
    }
}

/**
 * Entity for fonts parsing
 */
data class FontModel(
    val fontFamily: String = "",
    val fontWeight: String = "",
    val fontSize: Int = 0,
    val lineHeight: Int = 0,
    val letterSpacing: Int = 0
)