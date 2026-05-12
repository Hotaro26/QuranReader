package com.hotaro.quranreader.data.model

data class RamadanDay(
    val day: Int,
    val fasted: Boolean = false,
    val prayedTaraweeh: Boolean = false,
    val quranRead: Boolean = false
)
