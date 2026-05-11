package com.hotaro.quranreader.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.model.Ayah
import com.hotaro.quranreader.data.model.Bookmark
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AyahUiModel(
    val numberInSurah: Int,
    val arabicText: String,
    val translationText: String,
    val isBookmarked: Boolean = false
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _ayahs = MutableStateFlow<List<AyahUiModel>>(emptyList())
    val ayahs = _ayahs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentSurah = MutableStateFlow<Int?>(null)
    
    val bookmarks = repository.getAllBookmarks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadSurah(surahNumber: Int) {
        _currentSurah.value = surahNumber
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val translationEdition = repository.selectedTranslation.first().replace("_", "-")
                Log.d("ReaderViewModel", "Loading surah $surahNumber with translation $translationEdition")
                
                val arabicAyahs = repository.getSurahAyahs("ara-quranuthmanienc", surahNumber)
                val translationAyahs = repository.getSurahAyahs(translationEdition, surahNumber)
                
                val combined = arabicAyahs.zip(translationAyahs) { ar, tr ->
                    AyahUiModel(
                        numberInSurah = ar.verse,
                        arabicText = ar.text,
                        translationText = tr.text
                    )
                }
                
                _ayahs.value = combined
            } catch (e: Exception) {
                Log.e("ReaderViewModel", "Error loading surah: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLastRead(ayahNumber: Int) {
        val surahNumber = _currentSurah.value ?: return
        viewModelScope.launch {
            repository.saveLastRead(surahNumber, ayahNumber)
        }
    }

    fun toggleBookmark(ayah: AyahUiModel) {
        val surahNumber = _currentSurah.value ?: return
        val surah = repository.getSurahs().find { it.number == surahNumber }
        val surahName = surah?.englishName ?: ""
        
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.surahNumber == surahNumber && it.ayahNumber == ayah.numberInSurah }
            if (isBookmarked) {
                repository.removeBookmarkBySurahAyah(surahNumber, ayah.numberInSurah)
            } else {
                repository.addBookmark(
                    Bookmark(
                        surahNumber = surahNumber,
                        ayahNumber = ayah.numberInSurah,
                        surahName = surahName,
                        text = ayah.arabicText
                    )
                )
            }
        }
    }
}
