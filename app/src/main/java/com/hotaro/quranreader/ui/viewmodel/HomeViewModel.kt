package com.hotaro.quranreader.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.model.Bookmark
import com.hotaro.quranreader.data.model.PrayerTime
import com.hotaro.quranreader.data.model.PrayerTimeProvider
import com.hotaro.quranreader.data.model.Surah
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val lastReadSurah: Surah? = null,
    val lastReadAyah: Int = 1,
    val bookmarks: List<Bookmark> = emptyList(),
    val currentTime: String = "",
    val prayerTimes: List<PrayerTime> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _currentTime = MutableStateFlow("")
    
    init {
        updateTime()
    }

    private fun updateTime() {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis().toString()
                delay(1000 * 60) // Update every minute
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repository.lastReadSurah,
        repository.lastReadAyah,
        repository.getAllBookmarks(),
        repository.use24HourFormat,
        _currentTime
    ) { surahNum, ayahNum, bookmarks, use24Hour, _ ->
        val surah = repository.getSurahs().find { it.number == surahNum }
        val sdf = if (use24Hour) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }
        val formattedTime = sdf.format(Date())

        HomeUiState(
            lastReadSurah = surah,
            lastReadAyah = ayahNum,
            bookmarks = bookmarks,
            currentTime = formattedTime,
            prayerTimes = PrayerTimeProvider.getPrayerTimes(use24Hour)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
