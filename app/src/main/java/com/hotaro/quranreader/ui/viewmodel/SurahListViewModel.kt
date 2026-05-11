package com.hotaro.quranreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.hotaro.quranreader.data.model.Surah
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs = _surahs.asStateFlow()

    init {
        _surahs.value = repository.getSurahs()
    }
}
