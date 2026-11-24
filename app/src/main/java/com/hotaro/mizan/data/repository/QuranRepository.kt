package com.hotaro.mizan.data.repository

import com.hotaro.mizan.data.SurahData
import com.hotaro.mizan.data.local.*
import com.hotaro.mizan.data.model.*
import com.hotaro.mizan.data.remote.ExternalApiService
import com.hotaro.mizan.data.remote.QuranApiService
import com.hotaro.mizan.data.remote.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
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
    val showContinuePill = preferenceManager.showContinuePill
    val manualFajr = preferenceManager.manualFajr
    val manualDhuhr = preferenceManager.manualDhuhr
    val manualAsr = preferenceManager.manualAsr
    val manualMaghrib = preferenceManager.manualMaghrib
    val manualIsha = preferenceManager.manualIsha
    val selectedAppIcon = preferenceManager.selectedAppIcon
    val navBarBlurStyle = preferenceManager.navBarBlurStyle
    val windowAnimationStyle = preferenceManager.windowAnimationStyle
    val useClassicNavBar = preferenceManager.useClassicNavBar
    val prayerReminderEnabled = preferenceManager.prayerReminderEnabled
    val prayerReminderMinutesBefore = preferenceManager.prayerReminderMinutesBefore
    val dismissedUpdateTag = preferenceManager.dismissedUpdateTag

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

    suspend fun saveShowContinuePill(show: Boolean) {
        preferenceManager.saveShowContinuePill(show)
    }

    suspend fun saveManualPrayerTime(prayer: String, time: String) {
        preferenceManager.saveManualPrayerTime(prayer, time)
    }

    suspend fun saveSelectedAppIcon(icon: String) {
        preferenceManager.saveSelectedAppIcon(icon)
    }

    suspend fun saveNavBarBlurStyle(style: String) {
        preferenceManager.saveNavBarBlurStyle(style)
    }

    suspend fun saveWindowAnimationStyle(style: String) {
        preferenceManager.saveWindowAnimationStyle(style)
    }

    suspend fun saveUseClassicNavBar(enabled: Boolean) {
        preferenceManager.saveUseClassicNavBar(enabled)
    }

    suspend fun savePrayerReminderEnabled(enabled: Boolean) {
        preferenceManager.savePrayerReminderEnabled(enabled)
    }

    suspend fun savePrayerReminderMinutesBefore(minutes: Int) {
        preferenceManager.savePrayerReminderMinutesBefore(minutes)
    }

    suspend fun saveCachedPrayerTimes(fajr: String, dhuhr: String, asr: String, maghrib: String, isha: String) {
        preferenceManager.saveCachedPrayerTimes(fajr, dhuhr, asr, maghrib, isha)
    }

    suspend fun saveDismissedUpdateTag(tag: String) {
        preferenceManager.saveDismissedUpdateTag(tag)
    }

    // Tasbih
    val tasbihSelectedDhikrIndex = preferenceManager.tasbihSelectedDhikrIndex
    val tasbihCurrentCount = preferenceManager.tasbihCurrentCount
    val tasbihTargetCount = preferenceManager.tasbihTargetCount
    val tasbihLifetimeCount = preferenceManager.tasbihLifetimeCount
    val tasbihDailyCount = preferenceManager.tasbihDailyCount
    val tasbihLastDate = preferenceManager.tasbihLastDate
    val tasbihVibrationEnabled = preferenceManager.tasbihVibrationEnabled
    val tasbihSoundEnabled = preferenceManager.tasbihSoundEnabled
    val tasbihCardPage = preferenceManager.tasbihCardPage

    suspend fun saveTasbihState(
        selectedDhikrIndex: Int,
        currentCount: Int,
        targetCount: Int,
        dailyCount: Int,
        lifetimeCount: Int,
        lastDate: String
    ) {
        preferenceManager.saveTasbihState(
            selectedDhikrIndex,
            currentCount,
            targetCount,
            dailyCount,
            lifetimeCount,
            lastDate
        )
    }

    suspend fun saveTasbihVibration(enabled: Boolean) {
        preferenceManager.saveTasbihVibration(enabled)
    }

    suspend fun saveTasbihSound(enabled: Boolean) {
        preferenceManager.saveTasbihSound(enabled)
    }

    suspend fun saveTasbihCardPage(page: Int) {
        preferenceManager.saveTasbihCardPage(page)
    }

    // Surah List
    fun getSurahs(): List<Surah> {
        return SurahData.surahs
    }

    // GitHub Releases & Changelog
    suspend fun fetchGithubReleases(): List<ReleaseNote> {
        try {
            val response = externalApiService.getGithubReleases()
            if (response.isSuccessful) {
                val releases = response.body()
                if (!releases.isNullOrEmpty()) {
                    val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val outFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
                    return releases.mapIndexed { index, release ->
                        val formattedDate = try {
                            val d = inFormat.parse(release.publishedAt ?: "")
                            if (d != null) outFormat.format(d) else release.publishedAt ?: ""
                        } catch (e: Exception) {
                            release.publishedAt ?: ""
                        }
                        val rawHighlights = release.body?.lines()
                            ?.map { it.trim().removePrefix("-").removePrefix("*").trim() }
                            ?.filter { it.isNotEmpty() }
                            ?: emptyList()
                        val highlights = if (rawHighlights.isNotEmpty()) {
                            rawHighlights
                        } else {
                            listOf(release.name?.takeIf { it.isNotBlank() } ?: "Release ${release.tagName ?: ""}")
                        }
                        val tags = mutableListOf<String>()
                        if (index == 0) tags.add("Latest")
                        if (release.prerelease) tags.add("Pre-release") else tags.add("Stable")
                        if (!release.assets.isNullOrEmpty()) tags.add("APK available")

                        ReleaseNote(
                            version = release.tagName ?: release.name ?: "v1.0.0",
                            title = release.name ?: release.tagName ?: "Release",
                            date = formattedDate,
                            isLatest = index == 0,
                            tags = tags,
                            highlights = highlights,
                            description = release.body,
                            downloadUrl = release.assets?.firstOrNull { it.name?.endsWith(".apk") == true }?.browserDownloadUrl,
                            htmlUrl = release.htmlUrl
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}
