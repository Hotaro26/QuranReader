package com.hotaro.quranreader.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener, LocationListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _qiblaDirection = MutableStateFlow(0f)
    val qiblaDirection = _qiblaDirection.asStateFlow()

    private val _compassHeading = MutableStateFlow(0f)
    val compassHeading = _compassHeading.asStateFlow()

    private val _hasLocation = MutableStateFlow(false)
    val hasLocation = _hasLocation.asStateFlow()
    
    private val kaabaLat = 21.422487
    val kaabaLon = 39.826206

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var hasAccelerometer = false
    private var hasMagnetometer = false

    fun startSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
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
            e.printStackTrace()
        }

        if (bestLocation != null) {
            updateQiblaDirection(bestLocation.latitude, bestLocation.longitude)
        }

        // Request updates
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, this)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateQiblaDirection(lat: Double, lon: Double) {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(lon)
        val lat2 = Math.toRadians(kaabaLat)
        val lon2 = Math.toRadians(kaabaLon)

        val dLon = lon2 - lon1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        var bearing = Math.toDegrees(Math.atan2(y, x)).toFloat()
        bearing = (bearing + 360.0f) % 360.0f
        
        _qiblaDirection.value = bearing
        _hasLocation.value = true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            hasAccelerometer = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            hasMagnetometer = true
        }

        if (hasAccelerometer && hasMagnetometer) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            
            var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            azimuth = (azimuth + 360) % 360
            _compassHeading.value = azimuth
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onLocationChanged(location: Location) {
        updateQiblaDirection(location.latitude, location.longitude)
    }
    // For older Android versions
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}
