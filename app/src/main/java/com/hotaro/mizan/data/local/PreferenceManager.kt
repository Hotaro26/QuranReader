package com.hotaro.mizan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quran_prefs")

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
        preferences[PreferencesKeys.SHOW_CONTINUE_PILL] ?: true
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

}
