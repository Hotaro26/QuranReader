package com.hotaro.mizan.data.remote

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
        @Query("current_weather") current_weather: Boolean = true,
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>

    // AlAdhan API for Prayer Times
    @GET("https://api.aladhan.com/v1/timings")
    suspend fun getPrayerTimings(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("method") method: Int = 2
    ): Response<AlAdhanResponse>

    // GitHub API for Releases & Changelogs
    @GET("https://api.github.com/repos/Hotaro26/QuranReader/releases")
    suspend fun getGithubReleases(
        @retrofit2.http.Header("User-Agent") userAgent: String = "Mizan-App"
    ): Response<List<GithubReleaseDto>>
}

data class AlAdhanResponse(
    val data: AlAdhanData
)

data class AlAdhanData(
    val timings: AlAdhanTimings
)

data class AlAdhanTimings(
    val Fajr: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String
)

data class HolidayDto(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String
)

data class WeatherResponse(
    val daily: WeatherDailyDto,
    val current_weather: CurrentWeatherDto?
)

data class CurrentWeatherDto(
    val temperature: Double,
    val weathercode: Int,
    val is_day: Int,
    val time: String
)

data class WeatherDailyDto(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weathercode: List<Int>
)

data class GithubReleaseDto(
    @com.google.gson.annotations.SerializedName("id") val id: Long?,
    @com.google.gson.annotations.SerializedName("name") val name: String?,
    @com.google.gson.annotations.SerializedName("tag_name") val tagName: String?,
    @com.google.gson.annotations.SerializedName("published_at") val publishedAt: String?,
    @com.google.gson.annotations.SerializedName("body") val body: String?,
    @com.google.gson.annotations.SerializedName("html_url") val htmlUrl: String?,
    @com.google.gson.annotations.SerializedName("prerelease") val prerelease: Boolean = false,
    @com.google.gson.annotations.SerializedName("assets") val assets: List<GithubAssetDto>? = null
)

data class GithubAssetDto(
    @com.google.gson.annotations.SerializedName("name") val name: String?,
    @com.google.gson.annotations.SerializedName("size") val size: Long?,
    @com.google.gson.annotations.SerializedName("download_count") val downloadCount: Int?,
    @com.google.gson.annotations.SerializedName("browser_download_url") val browserDownloadUrl: String?
)
