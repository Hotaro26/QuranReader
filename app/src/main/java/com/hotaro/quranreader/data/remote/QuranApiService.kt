package com.hotaro.quranreader.data.remote

import com.hotaro.quranreader.data.model.Ayah
import com.hotaro.quranreader.data.model.Edition
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {

    @GET("editions.json")
    suspend fun getEditions(): Map<String, EditionDto>

    @GET("editions/{edition}/{surahNumber}.json")
    suspend fun getSurah(
        @Path("edition") edition: String,
        @Path("surahNumber") surahNumber: Int
    ): SurahResponse

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/fawazahmed0/quran-api/1/"
    }
}

data class EditionDto(
    val name: String,
    val author: String,
    val language: String,
    val direction: String,
    val source: String
)

fun EditionDto.toDomain(identifier: String) = Edition(
    identifier = identifier,
    name = name,
    author = author,
    language = language,
    direction = direction,
    source = source
)

data class SurahResponse(
    val chapter: List<AyahDto>
)

data class AyahDto(
    val chapter: Int,
    val verse: Int,
    val text: String
)

fun AyahDto.toDomain() = Ayah(
    chapter = chapter,
    verse = verse,
    text = text
)
