package com.hotaro.quranreader.data.model

import java.text.SimpleDateFormat
import java.util.*

data class PrayerTime(
    val name: String,
    val time: String,
    val isNext: Boolean = false
)

object PrayerTimeProvider {
    fun getPrayerTimes(use24HourFormat: Boolean = true): List<PrayerTime> {
        val rawTimes = listOf(
            "Fajr" to "04:30",
            "Dhuhr" to "12:15",
            "Asr" to "15:45",
            "Maghrib" to "18:45",
            "Isha" to "20:00"
        )

        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = if (use24HourFormat) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

        return rawTimes.map { (name, time) ->
            val date = inputFormat.parse(time) ?: Date()
            val formattedTime = outputFormat.format(date)
            PrayerTime(name, formattedTime, isNext = isNextPrayer(time))
        }
    }

    private fun isNextPrayer(prayerTime: String): Boolean {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMin = now.get(Calendar.MINUTE)
        
        val parts = prayerTime.split(":")
        val prayerHour = parts[0].toInt()
        val prayerMin = parts[1].toInt()
        
        val currentTimeInMins = currentHour * 60 + currentMin
        val prayerTimeInMins = prayerHour * 60 + prayerMin
        
        // This is a very simplified check
        return prayerTimeInMins > currentTimeInMins
    }
}
