package com.hotaro.mizan

import android.app.Application
import com.hotaro.mizan.reminder.PrayerReminderManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MizanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PrayerReminderManager.createNotificationChannel(this)
        PrayerReminderManager.scheduleReminders(this)
    }
}