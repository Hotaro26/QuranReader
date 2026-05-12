package com.hotaro.quranreader.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExternalApiService {

    // Nager.Date API for Public Holidays (No key required)
    @GET("https://date.nager.at/api/v3/NextPublicHolidays/{countryCode}")
    suspend fun getUpcomingHolidays(
        @Path("countryCode") countryCode: String
    ): Response<List<HolidayDto>>

    // Open-Meteo API for Weather (No key required)
    @GET("https://api.open-meteo.com/v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode",
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>
}

data class HolidayDto(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String
)

data class WeatherResponse(
    val daily: WeatherDailyDto
)

data class WeatherDailyDto(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weathercode: List<Int>
)
