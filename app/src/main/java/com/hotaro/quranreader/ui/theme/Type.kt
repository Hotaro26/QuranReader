package com.hotaro.quranreader.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hotaro.quranreader.R

val DefaultFont = FontFamily.Default
val HeyComic = FontFamily(Font(R.font.hey_comic))
val KgHappy = FontFamily(Font(R.font.kghappy))
val MatchaCih = FontFamily(Font(R.font.matcha_cih))
val Nirakolu = FontFamily(Font(R.font.nirakolu))
val RomanticSunrise = FontFamily(Font(R.font.romantic_sunrise))
val Takeover = FontFamily(Font(R.font.takeover))
val Takeover3d = FontFamily(Font(R.font.takeover_3d))
val ScratchBoys = FontFamily(Font(R.font.scratch_boys))

fun getTypography(fontName: String): Typography {
    val mainFontFamily = when (fontName) {
        "hey_comic" -> HeyComic
        "kghappy" -> KgHappy
        "matcha_cih" -> MatchaCih
        "nirakolu" -> Nirakolu
        "romantic_sunrise" -> RomanticSunrise
        "takeover" -> Takeover
        "takeover_3d" -> Takeover3d
        "scratch_boys" -> ScratchBoys
        else -> DefaultFont
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = mainFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = DefaultFont,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        ),
    )
}

val Typography = getTypography("default")
