package com.hotaro.quranreader.data.repository

import com.hotaro.quranreader.data.SurahData
import com.hotaro.quranreader.data.local.*
import com.hotaro.quranreader.data.model.*
import com.hotaro.quranreader.data.remote.ExternalApiService
import com.hotaro.quranreader.data.remote.QuranApiService
import com.hotaro.quranreader.data.remote.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor(
    private val apiService: QuranApiService,
    private val externalApiService: ExternalApiService,
    private val bookmarkDao: BookmarkDao,
    private val todoDao: TodoDao,
    private val ramadanDao: RamadanDao,
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

    // Todos
    fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addTodo(todo: Todo) {
        todoDao.insertTodo(todo.toEntity())
    }

    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo.toEntity())
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo.toEntity())
    }

    suspend fun clearCompletedTodos() {
        todoDao.clearCompletedTodos()
    }

    // Ramadan Tracker
    fun getAllRamadanDays(): Flow<List<RamadanDay>> {
        return ramadanDao.getAllDays().map { entities ->
            if (entities.isEmpty()) {
                // Initialize if empty
                val initialDays = (1..30).map { RamadanDay(it) }
                initialDays
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    suspend fun updateRamadanDay(day: RamadanDay) {
        ramadanDao.insertDay(day.toEntity())
    }

    suspend fun updateRamadanFasted(day: Int, fasted: Boolean) {
        ramadanDao.updateFasted(day, fasted)
    }

    suspend fun updateRamadanTaraweeh(day: Int, prayed: Boolean) {
        ramadanDao.updateTaraweeh(day, prayed)
    }

    suspend fun updateRamadanQuranRead(day: Int, read: Boolean) {
        ramadanDao.updateQuranRead(day, read)
    }

    // External APIs
    suspend fun getUpcomingHolidays(countryCode: String) = 
        externalApiService.getUpcomingHolidays(countryCode)

    suspend fun getWeather(lat: Double, lon: Double) = 
        externalApiService.getWeather(lat, lon)

    suspend fun getPrayerTimings(lat: Double, lon: Double, method: Int) =
        externalApiService.getPrayerTimings(lat, lon, method)

    // Preferences
    val lastReadSurah = preferenceManager.lastReadSurah
    val lastReadAyah = preferenceManager.lastReadAyah
    val selectedTranslation = preferenceManager.selectedTranslation
    val themeMode = preferenceManager.themeMode
    val colorPalette = preferenceManager.colorPalette
    val appFont = preferenceManager.appFont
    val use24HourFormat = preferenceManager.use24HourFormat
    val ramadanModeActive = preferenceManager.ramadanModeActive
    val ramadanRegion = preferenceManager.ramadanRegion
    val hasCompletedOnboarding = preferenceManager.hasCompletedOnboarding
    val prayerCalculationMethod = preferenceManager.prayerCalculationMethod

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

    suspend fun saveRamadanModeActive(active: Boolean) {
        preferenceManager.saveRamadanModeActive(active)
    }

    suspend fun saveRamadanRegion(region: String) {
        preferenceManager.saveRamadanRegion(region)
    }

    suspend fun saveHasCompletedOnboarding(completed: Boolean) {
        preferenceManager.saveHasCompletedOnboarding(completed)
    }

    suspend fun savePrayerCalculationMethod(method: Int) {
        preferenceManager.savePrayerCalculationMethod(method)
    }

    // Surah List
    fun getSurahs(): List<Surah> {
        return SurahData.surahs
    }
}
