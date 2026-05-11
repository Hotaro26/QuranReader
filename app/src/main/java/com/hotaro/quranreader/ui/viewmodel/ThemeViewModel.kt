package com.hotaro.quranreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    repository: QuranRepository
) : ViewModel() {

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
}
