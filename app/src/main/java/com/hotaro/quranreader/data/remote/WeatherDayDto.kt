package com.hotaro.quranreader.data.remote

data class WeatherDayDto(
    val max: Double,
    val min: Double,
    val current: Double? = null,
    val code: Int
)
