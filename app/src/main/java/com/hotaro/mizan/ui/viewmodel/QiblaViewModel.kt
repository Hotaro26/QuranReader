package com.hotaro.mizan.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class QiblaViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener, LocationListener {

    companion object {
        private const val TAG = "QiblaViewModel"
        const val KAABA_LAT = 21.422487
        const val KAABA_LON = 39.826206
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _qiblaDirection = MutableStateFlow(0f)
    val qiblaDirection = _qiblaDirection.asStateFlow()

    private val _compassHeading = MutableStateFlow(0f)
    val compassHeading = _compassHeading.asStateFlow()

    private val _pitch = MutableStateFlow(0f)
    val pitch = _pitch.asStateFlow()

    private val _roll = MutableStateFlow(0f)
    val roll = _roll.asStateFlow()

    private val _isLevel = MutableStateFlow(true)
    val isLevel = _isLevel.asStateFlow()

    private val _distanceToKaabaKm = MutableStateFlow(0)
    val distanceToKaabaKm = _distanceToKaabaKm.asStateFlow()

    private val _currentCity = MutableStateFlow("")
    val currentCity = _currentCity.asStateFlow()

    private val _currentCoordinates = MutableStateFlow("")
    val currentCoordinates = _currentCoordinates.asStateFlow()

    private val _sensorAccuracy = MutableStateFlow(SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
    val sensorAccuracy = _sensorAccuracy.asStateFlow()

    private val _hasLocation = MutableStateFlow(false)
    val hasLocation = _hasLocation.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var hasRotationVector = false
    private var hasAccelerometer = false
    private var hasMagnetometer = false

    private var smoothedHeading = 0f
    private var smoothedPitch = 0f
    private var smoothedRoll = 0f

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchLocation()
            kotlinx.coroutines.delay(800)
            _isRefreshing.value = false
        }
    }

    fun startSensors() {
        val rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotSensor != null) {
            hasRotationVector = true
            sensorManager.registerListener(this, rotSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            hasRotationVector = false
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
        try {
            locationManager.removeUpdates(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing location updates: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        var bestLocation: Location? = null
        try {
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching last known location: ${e.message}")
        }

        if (bestLocation != null) {
            applyLocation(bestLocation)
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, this)
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting location updates: ${e.message}")
        }
    }

    private fun applyLocation(location: Location) {
        val lat = location.latitude
        val lon = location.longitude

        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(lon)
        val lat2 = Math.toRadians(KAABA_LAT)
        val lon2 = Math.toRadians(KAABA_LON)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        bearing = (bearing + 360.0f) % 360.0f

        _qiblaDirection.value = bearing
        _distanceToKaabaKm.value = calculateDistanceToKaaba(lat, lon)
        _currentCoordinates.value = String.format(Locale.US, "%.4f° %s, %.4f° %s",
            Math.abs(lat), if (lat >= 0) "N" else "S",
            Math.abs(lon), if (lon >= 0) "E" else "W"
        )
        _hasLocation.value = true

        reverseGeocode(lat, lon)
    }

    private fun calculateDistanceToKaaba(lat: Double, lon: Double): Int {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(KAABA_LAT - lat)
        val dLon = Math.toRadians(KAABA_LON - lon)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(KAABA_LAT)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toInt()
    }

    private fun reverseGeocode(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                val address = addresses?.firstOrNull()
                if (address != null) {
                    val city = address.locality ?: address.subAdminArea ?: address.adminArea
                    val country = address.countryName
                    if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                        _currentCity.value = "$city, $country"
                    } else if (!city.isNullOrBlank()) {
                        _currentCity.value = city
                    } else if (!country.isNullOrBlank()) {
                        _currentCity.value = country
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Geocoder reverse geocode failed: ${e.message}")
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            processRotationMatrix()
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                hasAccelerometer = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                hasMagnetometer = true
            }

            if (hasAccelerometer && hasMagnetometer) {
                val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
                if (success) {
                    processRotationMatrix()
                }
            }
        }
    }

    private fun processRotationMatrix() {
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        var azimuthDeg = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        azimuthDeg = (azimuthDeg + 360f) % 360f

        val pitchDeg = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
        val rollDeg = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

        // Low-pass filter for buttery smooth jitter-free rotation
        if (smoothedHeading == 0f) {
            smoothedHeading = azimuthDeg
            smoothedPitch = pitchDeg
            smoothedRoll = rollDeg
        } else {
            var diff = (azimuthDeg - smoothedHeading + 540f) % 360f - 180f
            smoothedHeading = (smoothedHeading + diff * 0.22f + 360f) % 360f
            smoothedPitch += (pitchDeg - smoothedPitch) * 0.20f
            smoothedRoll += (rollDeg - smoothedRoll) * 0.20f
        }

        _compassHeading.value = smoothedHeading
        _pitch.value = smoothedPitch
        _roll.value = smoothedRoll
        _isLevel.value = Math.abs(smoothedPitch) < 22f && Math.abs(smoothedRoll) < 22f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_ROTATION_VECTOR || sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            _sensorAccuracy.value = accuracy
        }
    }

    override fun onLocationChanged(location: Location) {
        applyLocation(location)
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}
