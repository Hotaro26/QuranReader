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
    val hasCompletedOnboarding: Boolean = false
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
        repository.hasCompletedOnboarding
    ) { mode, palette, font, blur, onboarding ->
        AppThemeConfig(
            themeMode = mode,
            colorPalette = palette,
            appFont = font,
            navBarBlurStyle = blur,
            hasCompletedOnboarding = onboarding
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
}
