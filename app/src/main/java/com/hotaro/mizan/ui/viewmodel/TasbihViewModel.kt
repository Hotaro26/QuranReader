package com.hotaro.mizan.ui.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.mizan.data.model.DefaultDhikrs
import com.hotaro.mizan.data.model.DhikrItem
import com.hotaro.mizan.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val repository: QuranRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val dhikrs: List<DhikrItem> = DefaultDhikrs.list

    private val _selectedDhikrIndex = MutableStateFlow(0)
    val selectedDhikrIndex = _selectedDhikrIndex.asStateFlow()

    private val _currentCount = MutableStateFlow(0)
    val currentCount = _currentCount.asStateFlow()

    private val _targetCount = MutableStateFlow(33)
    val targetCount = _targetCount.asStateFlow()

    private val _dailyCount = MutableStateFlow(0)
    val dailyCount = _dailyCount.asStateFlow()

    private val _lifetimeCount = MutableStateFlow(0)
    val lifetimeCount = _lifetimeCount.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled = _vibrationEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(false)
    val soundEnabled = _soundEnabled.asStateFlow()

    private val _cardPage = MutableStateFlow(0)
    val cardPage = _cardPage.asStateFlow()

    val currentDhikr = _selectedDhikrIndex.map { index ->
        dhikrs.getOrElse(index) { dhikrs.first() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, dhikrs.first())

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Exception) {
        null
    }

    private var soundPool: SoundPool? = null
    private var clickSoundId = 0

    init {
        initSoundPool()
        loadPersistedState()
    }

    private fun initSoundPool() {
        try {
            val audioAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttrs)
                .build()
        } catch (e: Exception) {
            // SoundPool optional
        }
    }

    private fun loadPersistedState() {
        viewModelScope.launch {
            val index = repository.tasbihSelectedDhikrIndex.first()
            val count = repository.tasbihCurrentCount.first()
            val target = repository.tasbihTargetCount.first()
            val daily = repository.tasbihDailyCount.first()
            val lifetime = repository.tasbihLifetimeCount.first()
            val lastDate = repository.tasbihLastDate.first()
            val vib = repository.tasbihVibrationEnabled.first()
            val snd = repository.tasbihSoundEnabled.first()
            val savedCardPage = repository.tasbihCardPage.first()

            val today = getTodayDateString()
            val effectiveDaily = if (lastDate == today) daily else 0

            _selectedDhikrIndex.value = index.coerceIn(0, dhikrs.size - 1)
            _currentCount.value = count
            _targetCount.value = if (target > 0) target else dhikrs[index.coerceIn(0, dhikrs.size - 1)].defaultTarget
            _dailyCount.value = effectiveDaily
            _lifetimeCount.value = lifetime
            _vibrationEnabled.value = vib
            _soundEnabled.value = snd
            _cardPage.value = savedCardPage.coerceIn(0, 1)
        }
    }

    fun setCardPage(page: Int) {
        val clamped = page.coerceIn(0, 1)
        if (_cardPage.value != clamped) {
            _cardPage.value = clamped
            viewModelScope.launch {
                repository.saveTasbihCardPage(clamped)
            }
        }
    }

    fun increment() {
        val target = _targetCount.value
        val newCount = _currentCount.value + 1
        val newDaily = _dailyCount.value + 1
        val newLifetime = _lifetimeCount.value + 1

        val isMilestone = target > 0 && newCount % target == 0

        _currentCount.value = newCount
        _dailyCount.value = newDaily
        _lifetimeCount.value = newLifetime

        triggerFeedback(isMilestone = isMilestone)
        saveState()

        // Auto-advance in post-prayer 33/33/34 cycle
        if (isMilestone && _selectedDhikrIndex.value in 0..2) {
            val nextIdx = (_selectedDhikrIndex.value + 1) % 3
            if (nextIdx != 0) {
                _selectedDhikrIndex.value = nextIdx
                _targetCount.value = dhikrs[nextIdx].defaultTarget
                _currentCount.value = 0
                saveState()
            }
        }
    }

    fun decrement() {
        if (_currentCount.value > 0) {
            _currentCount.value -= 1
            if (_dailyCount.value > 0) _dailyCount.value -= 1
            if (_lifetimeCount.value > 0) _lifetimeCount.value -= 1
            triggerFeedback(isMilestone = false)
            saveState()
        }
    }

    fun reset() {
        _currentCount.value = 0
        saveState()
    }

    fun selectDhikr(index: Int) {
        if (index in dhikrs.indices) {
            _selectedDhikrIndex.value = index
            _targetCount.value = dhikrs[index].defaultTarget
            _currentCount.value = 0
            saveState()
        }
    }

    fun setTarget(target: Int) {
        _targetCount.value = target
        saveState()
    }

    fun toggleVibration() {
        val newState = !_vibrationEnabled.value
        _vibrationEnabled.value = newState
        viewModelScope.launch {
            repository.saveTasbihVibration(newState)
        }
    }

    fun toggleSound() {
        val newState = !_soundEnabled.value
        _soundEnabled.value = newState
        viewModelScope.launch {
            repository.saveTasbihSound(newState)
        }
    }

    private fun triggerFeedback(isMilestone: Boolean) {
        if (_vibrationEnabled.value) {
            try {
                if (isMilestone) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(60)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(25)
                    }
                }
            } catch (e: Exception) {
                // Ignore vibrator error
            }
        }
    }

    private fun saveState() {
        viewModelScope.launch {
            repository.saveTasbihState(
                selectedDhikrIndex = _selectedDhikrIndex.value,
                currentCount = _currentCount.value,
                targetCount = _targetCount.value,
                dailyCount = _dailyCount.value,
                lifetimeCount = _lifetimeCount.value,
                lastDate = getTodayDateString()
            )
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        soundPool?.release()
        soundPool = null
    }
}
