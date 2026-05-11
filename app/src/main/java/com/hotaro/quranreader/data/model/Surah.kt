package com.hotaro.quranreader.data.model

data class Surah(
    val number: Int,
    val englishName: String,
    val arabicName: String,
    val transliteration: String,
    val translation: String,
    val totalAyahs: Int,
    val revelationType: String
)
