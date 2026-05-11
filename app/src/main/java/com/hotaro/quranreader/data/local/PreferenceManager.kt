package com.hotaro.quranreader.data.local

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
        preferences[PreferencesKeys.COLOR_PALETTE] ?: "default"
    }

    val appFont: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_FONT] ?: "default"
    }

    val use24HourFormat: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_24_HOUR_FORMAT] ?: true
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
}
