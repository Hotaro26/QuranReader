package com.hotaro.mizan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quran_prefs")

@Singleton
class PreferenceManager @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val LAST_READ_SURAH = intPreferencesKey("last_read_surah")
        val LAST_READ_AYAH = intPreferencesKey("last_read_ayah")
        val SELECTED_TRANSLATION = stringPreferencesKey("selected_translation")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val COLOR_PALETTE = stringPreferencesKey("color_palette")
        val APP_FONT = stringPreferencesKey("app_font")
        val USE_24_HOUR_FORMAT = booleanPreferencesKey("use_24_hour_format")
        val RAMADAN_MODE_ACTIVE = booleanPreferencesKey("ramadan_mode_active")
        val RAMADAN_REGION = stringPreferencesKey("ramadan_region")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val PRAYER_CALCULATION_METHOD = intPreferencesKey("prayer_calculation_method")
        val SHOW_CONTINUE_PILL = booleanPreferencesKey("show_continue_pill")
        val MANUAL_FAJR = stringPreferencesKey("manual_fajr")
        val MANUAL_DHUHR = stringPreferencesKey("manual_dhuhr")
        val MANUAL_ASR = stringPreferencesKey("manual_asr")
        val MANUAL_MAGHRIB = stringPreferencesKey("manual_maghrib")
        val MANUAL_ISHA = stringPreferencesKey("manual_isha")
        val CACHED_FAJR = stringPreferencesKey("cached_fajr")
        val CACHED_DHUHR = stringPreferencesKey("cached_dhuhr")
        val CACHED_ASR = stringPreferencesKey("cached_asr")
        val CACHED_MAGHRIB = stringPreferencesKey("cached_maghrib")
        val CACHED_ISHA = stringPreferencesKey("cached_isha")
        val SELECTED_APP_ICON = stringPreferencesKey("selected_app_icon")
        val NAV_BAR_BLUR_STYLE = stringPreferencesKey("nav_bar_blur_style")
        val WINDOW_ANIMATION_STYLE = stringPreferencesKey("window_animation_style")
        val USE_CLASSIC_NAV_BAR = booleanPreferencesKey("use_classic_nav_bar")
        val PRAYER_REMINDER_ENABLED = booleanPreferencesKey("prayer_reminder_enabled")
        val PRAYER_REMINDER_MINUTES_BEFORE = intPreferencesKey("prayer_reminder_minutes_before")
        val DISMISSED_UPDATE_TAG = stringPreferencesKey("dismissed_update_tag")
        val TASBIH_SELECTED_DHIKR_INDEX = intPreferencesKey("tasbih_selected_dhikr_index")
        val TASBIH_CURRENT_COUNT = intPreferencesKey("tasbih_current_count")
        val TASBIH_TARGET_COUNT = intPreferencesKey("tasbih_target_count")
        val TASBIH_LIFETIME_COUNT = intPreferencesKey("tasbih_lifetime_count")
        val TASBIH_DAILY_COUNT = intPreferencesKey("tasbih_daily_count")
        val TASBIH_LAST_DATE = stringPreferencesKey("tasbih_last_date")
        val TASBIH_VIBRATION_ENABLED = booleanPreferencesKey("tasbih_vibration_enabled")
        val TASBIH_SOUND_ENABLED = booleanPreferencesKey("tasbih_sound_enabled")
        val TASBIH_CARD_PAGE = intPreferencesKey("tasbih_card_page")
    }

    val lastReadSurah: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_READ_SURAH] ?: 1
    }

    val lastReadAyah: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_READ_AYAH] ?: 1
    }

    val selectedTranslation: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_TRANSLATION] ?: "eng-mustafakhattabg"
    }

    val themeMode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: 0 // 0: System, 1: Light, 2: Dark
    }

    val colorPalette: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COLOR_PALETTE] ?: "dynamic"
    }

    val appFont: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_FONT] ?: "default"
    }

    val use24HourFormat: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_24_HOUR_FORMAT] ?: true
    }

    val ramadanModeActive: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RAMADAN_MODE_ACTIVE] ?: false
    }

    val ramadanRegion: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.RAMADAN_REGION] ?: "Middle East"
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    val prayerCalculationMethod: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PRAYER_CALCULATION_METHOD] ?: 2 // Default: 2 (ISNA)
    }

    val showContinuePill: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHOW_CONTINUE_PILL] ?: false
    }

    val manualFajr: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MANUAL_FAJR] ?: "05:00"
    }

    val manualDhuhr: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MANUAL_DHUHR] ?: "12:30"
    }

    val manualAsr: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MANUAL_ASR] ?: "15:45"
    }

    val manualMaghrib: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MANUAL_MAGHRIB] ?: "18:30"
    }

    val manualIsha: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MANUAL_ISHA] ?: "20:00"
    }

    val selectedAppIcon: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_APP_ICON] ?: "emerald"
    }

    val navBarBlurStyle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NAV_BAR_BLUR_STYLE] ?: "glass"
    }

    val windowAnimationStyle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WINDOW_ANIMATION_STYLE] ?: "default"
    }

    val useClassicNavBar: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_CLASSIC_NAV_BAR] ?: false
    }

    val prayerReminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PRAYER_REMINDER_ENABLED] ?: true
    }

    val prayerReminderMinutesBefore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PRAYER_REMINDER_MINUTES_BEFORE] ?: 10
    }

    val dismissedUpdateTag: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DISMISSED_UPDATE_TAG]
    }

    suspend fun saveLastRead(surah: Int, ayah: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_READ_SURAH] = surah
            preferences[PreferencesKeys.LAST_READ_AYAH] = ayah
        }
    }

    suspend fun saveSelectedTranslation(translation: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_TRANSLATION] = translation
        }
    }

    suspend fun saveThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun saveColorPalette(palette: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COLOR_PALETTE] = palette
        }
    }

    suspend fun saveAppFont(font: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_FONT] = font
        }
    }

    suspend fun saveUse24HourFormat(use24Hour: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_24_HOUR_FORMAT] = use24Hour
        }
    }

    suspend fun saveRamadanModeActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RAMADAN_MODE_ACTIVE] = active
        }
    }

    suspend fun saveRamadanRegion(region: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RAMADAN_REGION] = region
        }
    }

    suspend fun saveHasCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun savePrayerCalculationMethod(method: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRAYER_CALCULATION_METHOD] = method
        }
    }

    suspend fun saveShowContinuePill(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_CONTINUE_PILL] = show
        }
    }

    suspend fun saveManualPrayerTime(prayer: String, time: String) {
        context.dataStore.edit { preferences ->
            when (prayer.lowercase()) {
                "fajr" -> preferences[PreferencesKeys.MANUAL_FAJR] = time
                "dhuhr" -> preferences[PreferencesKeys.MANUAL_DHUHR] = time
                "asr" -> preferences[PreferencesKeys.MANUAL_ASR] = time
                "maghrib" -> preferences[PreferencesKeys.MANUAL_MAGHRIB] = time
                "isha" -> preferences[PreferencesKeys.MANUAL_ISHA] = time
            }
        }
    }

    suspend fun saveSelectedAppIcon(icon: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_APP_ICON] = icon
        }
    }

    suspend fun saveNavBarBlurStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NAV_BAR_BLUR_STYLE] = style
        }
    }

    suspend fun saveWindowAnimationStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WINDOW_ANIMATION_STYLE] = style
        }
    }

    suspend fun saveUseClassicNavBar(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_CLASSIC_NAV_BAR] = enabled
        }
    }

    suspend fun savePrayerReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRAYER_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun savePrayerReminderMinutesBefore(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PRAYER_REMINDER_MINUTES_BEFORE] = minutes
        }
    }

    suspend fun saveCachedPrayerTimes(fajr: String, dhuhr: String, asr: String, maghrib: String, isha: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CACHED_FAJR] = fajr
            preferences[PreferencesKeys.CACHED_DHUHR] = dhuhr
            preferences[PreferencesKeys.CACHED_ASR] = asr
            preferences[PreferencesKeys.CACHED_MAGHRIB] = maghrib
            preferences[PreferencesKeys.CACHED_ISHA] = isha
        }
    }

    suspend fun saveDismissedUpdateTag(tag: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DISMISSED_UPDATE_TAG] = tag
        }
    }

    val tasbihSelectedDhikrIndex: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_SELECTED_DHIKR_INDEX] ?: 0
    }

    val tasbihCurrentCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_CURRENT_COUNT] ?: 0
    }

    val tasbihTargetCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_TARGET_COUNT] ?: 33
    }

    val tasbihLifetimeCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_LIFETIME_COUNT] ?: 0
    }

    val tasbihDailyCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_DAILY_COUNT] ?: 0
    }

    val tasbihLastDate: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_LAST_DATE] ?: ""
    }

    val tasbihVibrationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_VIBRATION_ENABLED] ?: true
    }

    val tasbihSoundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_SOUND_ENABLED] ?: false
    }

    val tasbihCardPage: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TASBIH_CARD_PAGE] ?: 0
    }

    suspend fun saveTasbihCardPage(page: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASBIH_CARD_PAGE] = page
        }
    }

    suspend fun saveTasbihState(
        selectedDhikrIndex: Int,
        currentCount: Int,
        targetCount: Int,
        dailyCount: Int,
        lifetimeCount: Int,
        lastDate: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASBIH_SELECTED_DHIKR_INDEX] = selectedDhikrIndex
            preferences[PreferencesKeys.TASBIH_CURRENT_COUNT] = currentCount
            preferences[PreferencesKeys.TASBIH_TARGET_COUNT] = targetCount
            preferences[PreferencesKeys.TASBIH_DAILY_COUNT] = dailyCount
            preferences[PreferencesKeys.TASBIH_LIFETIME_COUNT] = lifetimeCount
            preferences[PreferencesKeys.TASBIH_LAST_DATE] = lastDate
        }
    }

    suspend fun saveTasbihVibration(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASBIH_VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun saveTasbihSound(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASBIH_SOUND_ENABLED] = enabled
        }
    }
}
