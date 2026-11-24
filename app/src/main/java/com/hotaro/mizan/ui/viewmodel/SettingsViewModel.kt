package com.hotaro.mizan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.mizan.data.model.Edition
import com.hotaro.mizan.data.model.ReleaseNote
import com.hotaro.mizan.data.model.Todo
import com.hotaro.mizan.data.repository.QuranRepository
import com.hotaro.mizan.reminder.PrayerReminderManager
import com.hotaro.mizan.util.AppIconManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: QuranRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _editions = MutableStateFlow<List<Edition>>(emptyList())
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _githubReleases = MutableStateFlow<List<ReleaseNote>>(emptyList())
    val githubReleases = _githubReleases.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate = _isCheckingUpdate.asStateFlow()

    private val _updateStatusMessage = MutableStateFlow<String?>(null)
    val updateStatusMessage = _updateStatusMessage.asStateFlow()

    init {
        fetchReleases(isManualCheck = false)
    }

    fun fetchReleases(isManualCheck: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            _updateStatusMessage.value = null
            try {
                val fetched = repository.fetchGithubReleases()
                if (fetched.isNotEmpty()) {
                    _githubReleases.value = fetched
                    if (isManualCheck) {
                        val latest = fetched.firstOrNull()
                        if (latest != null && com.hotaro.mizan.util.AppUpdateManager.isNewerVersion(com.hotaro.mizan.BuildConfig.VERSION_NAME, latest.version)) {
                            _updateStatusMessage.value = "New version ${latest.version} is available!"
                        } else {
                            _updateStatusMessage.value = "You are on the latest version (v${com.hotaro.mizan.BuildConfig.VERSION_NAME})"
                        }
                    }
                } else if (isManualCheck) {
                    _updateStatusMessage.value = "You are on the latest version (v${com.hotaro.mizan.BuildConfig.VERSION_NAME})"
                }
            } catch (e: Exception) {
                if (isManualCheck) {
                    _updateStatusMessage.value = "Failed to connect to GitHub. Showing local release notes."
                }
            } finally {
                _isCheckingUpdate.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(800)
            _isRefreshing.value = false
        }
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val themeMode = repository.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val colorPalette = repository.colorPalette.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dynamic")
    val use24HourFormat = repository.use24HourFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val selectedTranslation = repository.selectedTranslation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "eng-mustafakhattabg")
    val appFont = repository.appFont.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")
    val prayerCalculationMethod = repository.prayerCalculationMethod.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)
    val showContinuePill = repository.showContinuePill.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val manualFajr = repository.manualFajr.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "05:00")
    val manualDhuhr = repository.manualDhuhr.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "12:30")
    val manualAsr = repository.manualAsr.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "15:45")
    val manualMaghrib = repository.manualMaghrib.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "18:30")
    val manualIsha = repository.manualIsha.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "20:00")
    val selectedAppIcon = repository.selectedAppIcon.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "emerald")
    val navBarBlurStyle = repository.navBarBlurStyle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "glass")
    val windowAnimationStyle = repository.windowAnimationStyle.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")
    val useClassicNavBar = repository.useClassicNavBar.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val prayerReminderEnabled = repository.prayerReminderEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val prayerReminderMinutesBefore = repository.prayerReminderMinutesBefore.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10)

    fun sendTestNotification() {
        PrayerReminderManager.showTestNotification(context)
    }

    fun setPrayerReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.savePrayerReminderEnabled(enabled)
            PrayerReminderManager.scheduleReminders(context)
        }
    }

    fun setPrayerReminderMinutesBefore(minutes: Int) {
        viewModelScope.launch {
            repository.savePrayerReminderMinutesBefore(minutes)
            PrayerReminderManager.scheduleReminders(context)
        }
    }

    fun setNavBarBlurStyle(style: String) {
        viewModelScope.launch {
            repository.saveNavBarBlurStyle(style)
        }
    }

    fun setWindowAnimationStyle(style: String) {
        viewModelScope.launch {
            repository.saveWindowAnimationStyle(style)
        }
    }

    fun setUseClassicNavBar(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveUseClassicNavBar(enabled)
        }
    }

    fun setAppIcon(context: Context, icon: String) {
        viewModelScope.launch {
            repository.saveSelectedAppIcon(icon)
            AppIconManager.setAppIcon(context, icon)
        }
    }

    fun saveManualPrayerTime(prayer: String, time: String) {
        viewModelScope.launch {
            repository.saveManualPrayerTime(prayer, time)
            PrayerReminderManager.scheduleReminders(context)
        }
    }

    fun updateShowContinuePill(show: Boolean) {
        viewModelScope.launch {
            repository.saveShowContinuePill(show)
        }
    }

    val todoHistory = repository.getAllTodos().map { todos ->
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        todos.filter { it.isCompleted }
            .groupBy { sdf.format(Date(it.timestamp)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val filteredEditions = combine(_editions, _searchQuery) { editions, query ->
        if (query.isBlank()) {
            editions
        } else {
            editions.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true) ||
                it.language.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadEditions()
    }

    private fun loadEditions() {
        viewModelScope.launch {
            try {
                _editions.value = repository.getEditions()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTranslation(edition: Edition) {
        viewModelScope.launch {
            repository.saveSelectedTranslation(edition.identifier)
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            repository.saveThemeMode(mode)
        }
    }

    fun setColorPalette(palette: String) {
        viewModelScope.launch {
            repository.saveColorPalette(palette)
        }
    }

    fun setAppFont(font: String) {
        viewModelScope.launch {
            repository.saveAppFont(font)
        }
    }

    fun setUse24HourFormat(use24Hour: Boolean) {
        viewModelScope.launch {
            repository.saveUse24HourFormat(use24Hour)
        }
    }

    fun setPrayerCalculationMethod(method: Int) {
        viewModelScope.launch {
            repository.savePrayerCalculationMethod(method)
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
}
