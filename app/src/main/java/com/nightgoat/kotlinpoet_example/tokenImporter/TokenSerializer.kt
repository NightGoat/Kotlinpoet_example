package com.nightgoat.kotlinpoet_example.tokenImporter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object TokenSerializer : KSerializer<Document> {
    override val descriptor: SerialDescriptor
        get() = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): Document {
        require(decoder is JsonDecoder)
        kotlin.runCatching {
            val topObject = decoder.decodeJsonElement()
            val listOfComponents =
                topObject.jsonObject.mapNotNullTo(mutableListOf<Token<Any>>()) { nestedObject ->
                    val key = nestedObject.key
                    when {
                        TokenType.Colors.contains(key) -> {
                            val hex = nestedObject.value.toString().replace("\"", "")
                            Token(
                                name = key,
                                value = hex,
                                type = TokenType.Colors
                            )
                        }

                        TokenType.Sizes.contains(key) -> Token(
                            name = key,
                            value = nestedObject.value.jsonPrimitive.intOrNull ?: 0,
                            type = TokenType.Sizes
                        )

                        TokenType.Fonts.contains(key) -> deserializeFont(nestedObject)
                        else -> null
                    }
                }
            return Document(listOfComponents)
        }.onFailure {
            it.printStackTrace()
        }.getOrThrow()
    }

    override fun serialize(encoder: Encoder, value: Document) = Unit

    private fun deserializeFont(nestedObject: Map.Entry<String, JsonElement>): Token<Any>? {
        return if (nestedObject.value is JsonObject) {
            var fontFamily = ""
            var fontWeight = ""
            var lineHeight = 0
            var letterSpacing = 0
            var fontSize = 0
            nestedObject.value.jsonObject.forEach {
                when (it.key) {
                    "fontFamily" -> fontFamily = it.value.toString().replace("\"", "").lowercase()
                    "fontWeight" -> fontWeight = it.value.toString().replace("\"", "")
                    "lineHeight" -> lineHeight = it.value.jsonPrimitive.intOrNull ?: 0
                    "letterSpacing" -> letterSpacing = it.value.jsonPrimitive.intOrNull ?: 0
                    "fontSize" -> fontSize = it.value.jsonPrimitive.intOrNull ?: 0
                    else -> Unit
                }
            }
            Token(
                name = nestedObject.key,
                value = FontModel(
                    fontFamily = fontFamily,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontWeight = fontWeight,
                    letterSpacing = letterSpacing
                ),
                type = TokenType.Fonts
            )
        } else null
    }
}