package com.hotaro.quranreader.data.model

data class Bookmark(
    val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
