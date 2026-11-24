package com.hotaro.mizan.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hotaro.mizan.MainActivity
import com.hotaro.mizan.R
import com.hotaro.mizan.data.local.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

object PrayerReminderManager {
    const val CHANNEL_ID = "prayer_reminders_channel"
    const val CHANNEL_NAME = "Prayer Reminders"
    const val ACTION_PRAYER_ALARM = "com.hotaro.mizan.ACTION_PRAYER_ALARM"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_PRAYER_TIME = "extra_prayer_time"
    const val EXTRA_PRAYER_MINUTES_BEFORE = "extra_prayer_minutes_before"
    const val EXTRA_PRAYER_ID = "extra_prayer_id"

    val PRAYER_IDS = mapOf(
        "Fajr" to 101,
        "Dhuhr" to 102,
        "Asr" to 103,
        "Maghrib" to 104,
        "Isha" to 105
    )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily prayer times and reminder notifications"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleReminders(context: Context) {
        createNotificationChannel(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = context.dataStore.data.first()
                val isEnabled = prefs[booleanPreferencesKey("prayer_reminder_enabled")] ?: true
                if (!isEnabled) {
                    cancelReminders(context)
                    return@launch
                }

                val minutesBefore = prefs[intPreferencesKey("prayer_reminder_minutes_before")] ?: 10
                val method = prefs[intPreferencesKey("prayer_calculation_method")] ?: 2

                val fajr = (if (method == -1) prefs[stringPreferencesKey("manual_fajr")] else prefs[stringPreferencesKey("cached_fajr")])
                    ?: prefs[stringPreferencesKey("manual_fajr")] ?: "05:00"
                val dhuhr = (if (method == -1) prefs[stringPreferencesKey("manual_dhuhr")] else prefs[stringPreferencesKey("cached_dhuhr")])
                    ?: prefs[stringPreferencesKey("manual_dhuhr")] ?: "12:30"
                val asr = (if (method == -1) prefs[stringPreferencesKey("manual_asr")] else prefs[stringPreferencesKey("cached_asr")])
                    ?: prefs[stringPreferencesKey("manual_asr")] ?: "15:45"
                val maghrib = (if (method == -1) prefs[stringPreferencesKey("manual_maghrib")] else prefs[stringPreferencesKey("cached_maghrib")])
                    ?: prefs[stringPreferencesKey("manual_maghrib")] ?: "18:30"
                val isha = (if (method == -1) prefs[stringPreferencesKey("manual_isha")] else prefs[stringPreferencesKey("cached_isha")])
                    ?: prefs[stringPreferencesKey("manual_isha")] ?: "20:00"

                val prayers = listOf(
                    "Fajr" to fajr,
                    "Dhuhr" to dhuhr,
                    "Asr" to asr,
                    "Maghrib" to maghrib,
                    "Isha" to isha
                )

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return@launch

                for ((name, timeStr) in prayers) {
                    val id = PRAYER_IDS[name] ?: 100
                    scheduleSinglePrayer(context, alarmManager, id, name, timeStr, minutesBefore)
                }
                Log.d("PrayerReminderManager", "Scheduled ${prayers.size} prayer reminders successfully (lead: ${minutesBefore}m)")
            } catch (e: Exception) {
                Log.e("PrayerReminderManager", "Failed to schedule prayer reminders: ${e.message}")
            }
        }
    }

    private fun scheduleSinglePrayer(
        context: Context,
        alarmManager: AlarmManager,
        id: Int,
        name: String,
        rawTime: String,
        minutesBefore: Int
    ) {
        val cleanTime = rawTime.trim().take(5)
        val parts = cleanTime.split(":")
        if (parts.size < 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var triggerTime = calendar.timeInMillis - (minutesBefore * 60 * 1000L)
        if (triggerTime <= now) {
            // Already passed today, schedule for next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            triggerTime = calendar.timeInMillis - (minutesBefore * 60 * 1000L)
        }

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
            putExtra(EXTRA_PRAYER_ID, id)
            putExtra(EXTRA_PRAYER_NAME, name)
            putExtra(EXTRA_PRAYER_TIME, cleanTime)
            putExtra(EXTRA_PRAYER_MINUTES_BEFORE, minutesBefore)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e("PrayerReminderManager", "Error setting alarm for $name: ${e.message}")
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (ignored: Exception) {}
        }
    }

    fun cancelReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (id in PRAYER_IDS.values) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALARM
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
        Log.d("PrayerReminderManager", "Cancelled all prayer reminders")
    }

    fun showTestNotification(context: Context) {
        createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            999,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Mizan Prayer Reminder"
        val body = "Prayer notifications are working properly! You will receive alerts at scheduled prayer times."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        NotificationManagerCompat.from(context).notify(999, notification)
    }
}
