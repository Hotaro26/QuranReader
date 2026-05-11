package com.hotaro.quranreader.data.repository

import com.hotaro.quranreader.data.SurahData
import com.hotaro.quranreader.data.local.*
import com.hotaro.quranreader.data.model.*
import com.hotaro.quranreader.data.remote.QuranApiService
import com.hotaro.quranreader.data.remote.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val apiService: QuranApiService,
    private val bookmarkDao: BookmarkDao,
    private val preferenceManager: PreferenceManager
) {

    // Remote
    suspend fun getEditions(): List<Edition> {
        return apiService.getEditions().map { (_, dto) -> dto.toDomain(dto.name) }
    }

    suspend fun getSurahAyahs(edition: String, surahNumber: Int): List<Ayah> {
        return apiService.getSurah(edition, surahNumber).chapter.map { it.toDomain() }
    }

    // Local Bookmarks
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addBookmark(bookmark: Bookmark) {
        bookmarkDao.insertBookmark(bookmark.toEntity())
    }

    suspend fun removeBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark.toEntity())
    }

    suspend fun removeBookmarkBySurahAyah(surahNumber: Int, ayahNumber: Int) {
        bookmarkDao.deleteBookmarkBySurahAyah(surahNumber, ayahNumber)
    }

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(surahNumber, ayahNumber)
    }

    // Preferences
    val lastReadSurah = preferenceManager.lastReadSurah
    val lastReadAyah = preferenceManager.lastReadAyah
    val selectedTranslation = preferenceManager.selectedTranslation
    val themeMode = preferenceManager.themeMode
    val colorPalette = preferenceManager.colorPalette
    val appFont = preferenceManager.appFont
    val use24HourFormat = preferenceManager.use24HourFormat

    suspend fun saveLastRead(surah: Int, ayah: Int) {
        preferenceManager.saveLastRead(surah, ayah)
    }

    suspend fun saveSelectedTranslation(translation: String) {
        preferenceManager.saveSelectedTranslation(translation)
    }

    suspend fun saveThemeMode(mode: Int) {
        preferenceManager.saveThemeMode(mode)
    }

    suspend fun saveColorPalette(palette: String) {
        preferenceManager.saveColorPalette(palette)
    }

    suspend fun saveAppFont(font: String) {
        preferenceManager.saveAppFont(font)
    }

    suspend fun saveUse24HourFormat(use24Hour: Boolean) {
        preferenceManager.saveUse24HourFormat(use24Hour)
    }

    // Surah List
    fun getSurahs(): List<Surah> {
        return SurahData.surahs
    }
}
