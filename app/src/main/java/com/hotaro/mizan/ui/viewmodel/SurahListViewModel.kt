package com.hotaro.mizan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.mizan.data.model.Surah
import com.hotaro.mizan.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs = _surahs.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        _surahs.value = repository.getSurahs()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _surahs.value = repository.getSurahs()
            delay(800)
            _isRefreshing.value = false
        }
    }
}
