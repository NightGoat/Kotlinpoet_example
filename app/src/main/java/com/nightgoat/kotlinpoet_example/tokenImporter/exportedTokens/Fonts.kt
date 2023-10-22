package com.nightgoat.kotlinpoet_example.tokenImporter.exportedTokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.nightgoat.kotlinpoet_example.R

public object Fonts {
  public val fontA: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle(
                      fontFamily = FontFamily(Font(R.font.jetbrainsmono)),
                      fontSize = 42.sp,
                      lineHeight = 50.sp,
                      letterSpacing = 0.sp,
                      fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                  )


  public val fontB: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle(
                      fontFamily = FontFamily(Font(R.font.jetbrainsmono)),
                      fontSize = 30.sp,
                      lineHeight = 50.sp,
                      letterSpacing = 0.sp,
                      fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                  )

}
