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
import com.hotaro.quranreader.data.model.RamadanDay
import com.hotaro.quranreader.data.model.Todo
import com.hotaro.quranreader.data.remote.HolidayDto
import com.hotaro.quranreader.data.remote.WeatherDayDto
import com.hotaro.quranreader.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class TrackerUiState(
    val todos: List<Todo> = emptyList(),
    val ramadanDays: List<RamadanDay> = emptyList(),
    val daysToRamadan: Long = 0,
    val ramadanModeActive: Boolean = false,
    val selectedRegion: String = "Middle East",
    val heatmapData: Map<Long, Int> = emptyMap(), // Date (millis) to count
    val holidays: List<HolidayDto> = emptyList(),
    val weather: WeatherDayDto? = null,
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val countryName: String? = null
)

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val repository: QuranRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), LocationListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    private val _holidays = MutableStateFlow<List<HolidayDto>>(emptyList())
    private val _weather = MutableStateFlow<WeatherDayDto?>(null)
    private val _countryName = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TrackerUiState> = combine(
        repository.getAllTodos(),
        repository.getAllRamadanDays(),
        repository.ramadanModeActive,
        repository.ramadanRegion,
        _holidays,
        _weather,
        _countryName
    ) { args: Array<*> ->
        val todos = args[0] as List<Todo>
        val ramadanDays = args[1] as List<RamadanDay>
        val ramadanModeActive = args[2] as Boolean
        val ramadanRegion = args[3] as String
        val holidays = args[4] as List<HolidayDto>
        val weather = args[5] as WeatherDayDto?
        val countryName = args[6] as String?
        val heatmap = todos.filter { it.isCompleted }
            .groupBy { 
                val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }.mapValues { it.value.size }

        val now = Date()
        val daySdf = SimpleDateFormat("dd", Locale.getDefault())
        val monthSdf = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())

        TrackerUiState(
            todos = todos.filter { !isSameDay(it.timestamp, System.currentTimeMillis()) || !it.isCompleted },
            ramadanDays = ramadanDays,
            daysToRamadan = calculateDaysToRamadan(ramadanRegion),
            ramadanModeActive = ramadanModeActive,
            selectedRegion = ramadanRegion,
            heatmapData = heatmap,
            holidays = holidays,
            weather = weather,
            day = daySdf.format(now),
            month = monthSdf.format(now),
            year = yearSdf.format(now),
            countryName = countryName
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackerUiState())

    init {
        fetchLocation()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        Log.d("TrackerViewModel", "fetchLocation called")
        try {
            val providers = locationManager.getProviders(true)
            Log.d("TrackerViewModel", "Available providers: $providers")
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
            if (bestLocation != null) {
                Log.d("TrackerViewModel", "Best location found: $bestLocation")
                onLocationChanged(bestLocation)
            } else {
                Log.d("TrackerViewModel", "No cached location found, requesting updates...")
            }
            // Request updates for fresh data
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 1f, this)
        } catch (e: Exception) {
            Log.e("TrackerViewModel", "Error fetching location: ${e.message}")
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TrackerViewModel", "onLocationChanged: $location")
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                Log.d("TrackerViewModel", "Addresses found: $addresses")
                if (addresses?.isNotEmpty() == true) {
                    val countryCode = addresses[0].countryCode
                    _countryName.value = addresses[0].countryName
                    Log.d("TrackerViewModel", "Detected country: ${addresses[0].countryName} ($countryCode)")
                    fetchExternalData(countryCode ?: "US", location.latitude, location.longitude)
                }
            } catch (e: Exception) {
                Log.e("TrackerViewModel", "Error in onLocationChanged: ${e.message}")
            }
        }
    }

    private suspend fun fetchExternalData(countryCode: String, lat: Double, lon: Double) {
        try {
            val holidayResp = repository.getUpcomingHolidays(countryCode)
            _holidays.value = if (holidayResp.isSuccessful) holidayResp.body() ?: emptyList() else emptyList()
            
            val weatherResp = repository.getWeather(lat, lon)
            if (weatherResp.isSuccessful) {
                _weather.value = weatherResp.body()?.let {
                    WeatherDayDto(
                        max = it.daily.temperature_2m_max.firstOrNull() ?: 0.0,
                        min = it.daily.temperature_2m_min.firstOrNull() ?: 0.0,
                        current = it.current_weather?.temperature,
                        code = it.daily.weathercode.firstOrNull() ?: 0
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("TrackerViewModel", "Error fetching external data: ${e.message}")
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun calculateDaysToRamadan(region: String): Long {
        val today = Calendar.getInstance()
        val nextRamadan = Calendar.getInstance().apply {
            // Ramadan 2027 is estimated to start on February 8 (Middle East)
            // South Asia usually starts 1 day later
            set(Calendar.YEAR, 2027)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            val day = if (region == "South Asia") 9 else 8
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diff = nextRamadan.timeInMillis - today.timeInMillis
        return if (diff > 0) {
            TimeUnit.MILLISECONDS.toDays(diff)
        } else {
            0
        }
    }

    fun setRamadanModeActive(active: Boolean) {
        viewModelScope.launch {
            repository.saveRamadanModeActive(active)
        }
    }

    fun setRamadanRegion(region: String) {
        viewModelScope.launch {
            repository.saveRamadanRegion(region)
        }
    }

    fun addTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTodo(Todo(title = title))
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun clearCompletedTodos() {
        viewModelScope.launch {
            repository.clearCompletedTodos()
        }
    }

    fun updateRamadanFasted(day: Int, fasted: Boolean) {
        viewModelScope.launch {
            val currentDay = uiState.value.ramadanDays.find { it.day == day } ?: RamadanDay(day)
            repository.updateRamadanDay(currentDay.copy(fasted = fasted))
        }
    }

    fun updateRamadanTaraweeh(day: Int, prayed: Boolean) {
        viewModelScope.launch {
            val currentDay = uiState.value.ramadanDays.find { it.day == day } ?: RamadanDay(day)
            repository.updateRamadanDay(currentDay.copy(prayedTaraweeh = prayed))
        }
    }

    fun updateRamadanQuranRead(day: Int, read: Boolean) {
        viewModelScope.launch {
            val currentDay = uiState.value.ramadanDays.find { it.day == day } ?: RamadanDay(day)
            repository.updateRamadanDay(currentDay.copy(quranRead = read))
        }
    }
}
