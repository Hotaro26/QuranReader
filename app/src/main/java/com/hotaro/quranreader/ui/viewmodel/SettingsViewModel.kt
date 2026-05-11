package com.hotaro.quranreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.model.Edition
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _editions = MutableStateFlow<List<Edition>>(emptyList())
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val themeMode = repository.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val colorPalette = repository.colorPalette.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dynamic")
    val use24HourFormat = repository.use24HourFormat.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val selectedTranslation = repository.selectedTranslation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "eng-mustafakhattabg")

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

    fun setUse24HourFormat(use24Hour: Boolean) {
        viewModelScope.launch {
            repository.saveUse24HourFormat(use24Hour)
        }
    }
}
