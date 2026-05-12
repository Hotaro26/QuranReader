package com.hotaro.quranreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    val hasCompleted = repository.hasCompletedOnboarding

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.saveHasCompletedOnboarding(true)
        }
    }
}
