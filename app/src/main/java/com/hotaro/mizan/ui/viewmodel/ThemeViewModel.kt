package com.hotaro.mizan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.mizan.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppThemeConfig(
    val themeMode: Int = 0,
    val colorPalette: String = "dynamic",
    val appFont: String = "default",
    val navBarBlurStyle: String = "glass",
    val hasCompletedOnboarding: Boolean = false,
    val windowAnimationStyle: String = "default",
    val useClassicNavBar: Boolean = false
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    repository: QuranRepository
) : ViewModel() {

    val themeConfig: StateFlow<AppThemeConfig?> = combine(
        repository.themeMode,
        repository.colorPalette,
        repository.appFont,
        repository.navBarBlurStyle,
        repository.hasCompletedOnboarding,
        repository.windowAnimationStyle,
        repository.useClassicNavBar
    ) { args: Array<Any?> ->
        AppThemeConfig(
            themeMode = args[0] as? Int ?: 0,
            colorPalette = args[1] as? String ?: "dynamic",
            appFont = args[2] as? String ?: "default",
            navBarBlurStyle = args[3] as? String ?: "glass",
            hasCompletedOnboarding = args[4] as? Boolean ?: false,
            windowAnimationStyle = args[5] as? String ?: "default",
            useClassicNavBar = args[6] as? Boolean ?: false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    val themeMode = repository.themeMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val colorPalette = repository.colorPalette.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "dynamic"
    )

    val appFont = repository.appFont.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "default"
    )

    val navBarBlurStyle = repository.navBarBlurStyle.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "glass"
    )

    val windowAnimationStyle = repository.windowAnimationStyle.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "default"
    )

    val useClassicNavBar = repository.useClassicNavBar.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
}
