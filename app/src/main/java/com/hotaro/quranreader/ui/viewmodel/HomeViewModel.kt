package com.hotaro.quranreader.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.quranreader.data.model.Bookmark
import com.hotaro.quranreader.data.model.PrayerTime
import com.hotaro.quranreader.data.model.PrayerTimeProvider
import com.hotaro.quranreader.data.model.Surah
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val weatherTemp: Double? = null,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val cityName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuranRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), LocationListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _currentTime = MutableStateFlow("")
    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    private val _cityName = MutableStateFlow("")
    private val _weatherTemp = MutableStateFlow<Double?>(null)

    private val _homeAnimationTrigger = MutableSharedFlow<Unit>(replay = 0)
    val homeAnimationTrigger = _homeAnimationTrigger.asSharedFlow()

    fun triggerHomeAnimation() {
        viewModelScope.launch {
            _homeAnimationTrigger.emit(Unit)
        }
    }

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

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            if (bestLocation != null) {
                onLocationChanged(bestLocation)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, this)
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching location: ${e.message}")
        }
    }

    override fun onLocationChanged(location: Location) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea ?: ""
                _cityName.value = city

                val method = repository.prayerCalculationMethod.first()
                val prayerResponse = repository.getPrayerTimings(location.latitude, location.longitude, method)
                
                if (prayerResponse.isSuccessful) {
                    prayerResponse.body()?.data?.timings?.let { timings ->
                        val rawTimes = listOf(
                            "Fajr" to timings.Fajr,
                            "Dhuhr" to timings.Dhuhr,
                            "Asr" to timings.Asr,
                            "Maghrib" to timings.Maghrib,
                            "Isha" to timings.Isha
                        )
                        val use24Hour = repository.use24HourFormat.first()
                        _prayerTimes.value = PrayerTimeProvider.formatPrayerTimes(rawTimes, use24Hour)
                    }
                }

                val weatherResponse = repository.getWeather(location.latitude, location.longitude)
                if (weatherResponse.isSuccessful) {
                    weatherResponse.body()?.current_weather?.temperature?.let { temp ->
                        _weatherTemp.value = temp
                    }
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting data: ${e.message}")
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    val uiState: StateFlow<HomeUiState> = combine(
        repository.lastReadSurah,
        repository.lastReadAyah,
        repository.getAllBookmarks(),
        repository.use24HourFormat,
        _currentTime,
        _prayerTimes,
        _cityName,
        _weatherTemp
    ) { args ->
        val surahNum = args[0] as Int
        val ayahNum = args[1] as Int
        @Suppress("UNCHECKED_CAST") val bookmarks = args[2] as List<Bookmark>
        val use24Hour = args[3] as Boolean
        val currentTimeFlow = args[4] as String
        @Suppress("UNCHECKED_CAST") val apiPrayerTimes = args[5] as List<PrayerTime>
        val city = args[6] as String
        val weather = args[7] as Double?

        val surah = repository.getSurahs().find { it.number == surahNum }
        
        val now = Date()
        val timeSdf = if (use24Hour) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }
        val formattedTime = timeSdf.format(now)

        val daySdf = SimpleDateFormat("dd", Locale.getDefault())
        val monthSdf = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())

        val finalPrayerTimes = if (apiPrayerTimes.isNotEmpty()) apiPrayerTimes else PrayerTimeProvider.getPrayerTimes(use24Hour)

        HomeUiState(
            lastReadSurah = surah,
            lastReadAyah = ayahNum,
            bookmarks = bookmarks,
            currentTime = formattedTime,
            day = daySdf.format(now),
            month = monthSdf.format(now),
            year = yearSdf.format(now),
            weatherTemp = weather,
            prayerTimes = finalPrayerTimes,
            cityName = city
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
