package com.nightgoat.kotlinpoet_example.tokenImporter

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    //path to json with tokens
    val jsonFileText =
        File("app/src/main/java/com/nightgoat/kotlinpoet_example/tokenImporter/tokens.json").readText()
    val json = Json { ignoreUnknownKeys = true }
    //we deserialize it to Document.kt using TokenSerializer.kt
    val document = json.decodeFromString<Document>(string = jsonFileText)
    //then group all tokens by its type: ShapeTokenType (Fonts, Colors etc)
    val files = document.values.groupBy { it.type }
    //then we create token files themselves
    createFiles(files = files)
}

fun createFiles(files: Map<TokenType, List<Token<Any>>>) {
    files.forEach { typeFile ->
        //File builder
        val fileSpec = FileSpec.builder(
            ClassName(
                "com.nightgoat.kotlinpoet_example.tokenImporter.exportedTokens",
                typeFile.key.name
            )
        )
        //Object builder
        val obj = TypeSpec.objectBuilder(typeFile.key.name)
        typeFile.value.forEach { token ->
            runCatching {
                val value = token.value
                var type = token.value::class.simpleName.orEmpty()
                var statement: String? = null
                //Down here we are writing the parameters and give them initializers
                when (token.type) {
                    TokenType.Colors -> {
                        //This class is going to be a Color type and going to have an initializer
                        //val foo: androidx.compose.ui.graphics.Color = Color(value)
                        //(replace foo with name of the key in json and value with its value)
                        type = "androidx.compose.ui.graphics.Color"
                        statement = "Color(${value})"
                    }

                    TokenType.Sizes -> {
                        type = "androidx.compose.ui.unit.Dp"
                        fileSpec.addImport("androidx.compose.ui.unit", "dp")
                        statement = "${value}.dp"
                    }

                    TokenType.Fonts -> {
                        if (value is FontModel) {
                            type = "androidx.compose.ui.text.TextStyle"
                            fileSpec.addImport(
                                "androidx.compose.ui.text.font", "Font", "FontFamily"
                            )
                            fileSpec.addImport("com.nightgoat.kotlinpoet_example", "R")
                            fileSpec.addImport("androidx.compose.ui.unit", "sp")
                            statement = """
                                ${type}(
                                                fontFamily = FontFamily(Font(R.font.${value.fontFamily})),
                                                fontSize = ${value.fontSize}.sp,
                                                lineHeight = ${value.lineHeight}.sp,
                                                letterSpacing = ${value.letterSpacing}.sp,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.${value.fontWeight}
                                            )
                            """.trimIndent()
                        }
                    }
                }
                val initializer = CodeBlock.builder()
                statement?.let { initializer.addStatement(it) }
                //this will create a property inside of the class
                val property = PropertySpec.builder(token.name, ClassName("", type))
                    .initializer(initializer.build())
                obj.addProperty(property.build())
            }.onFailure {
                it.printStackTrace()
            }
        }
        fileSpec.addType(obj.build())
        val file = fileSpec.build()
        //File is being written to the shapeDesignAndroid module (tokens are needed there)
        file.writeTo(File("app/src/main/java"))
    }
}