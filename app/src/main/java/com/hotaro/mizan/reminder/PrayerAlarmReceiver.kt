package com.hotaro.mizan.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hotaro.mizan.MainActivity
import com.hotaro.mizan.R

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PrayerReminderManager.ACTION_PRAYER_ALARM) {
            val prayerId = intent.getIntExtra(PrayerReminderManager.EXTRA_PRAYER_ID, 100)
            val prayerName = intent.getStringExtra(PrayerReminderManager.EXTRA_PRAYER_NAME) ?: "Prayer"
            val prayerTime = intent.getStringExtra(PrayerReminderManager.EXTRA_PRAYER_TIME) ?: ""
            val minutesBefore = intent.getIntExtra(PrayerReminderManager.EXTRA_PRAYER_MINUTES_BEFORE, 0)

            Log.d("PrayerAlarmReceiver", "Triggered prayer alarm for $prayerName (lead: $minutesBefore mins)")

            PrayerReminderManager.createNotificationChannel(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Log.w("PrayerAlarmReceiver", "Notification permission not granted, skipping notification")
                    PrayerReminderManager.scheduleReminders(context)
                    return
                }
            }

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                prayerId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = if (minutesBefore == 0) {
                "Time for $prayerName Prayer"
            } else {
                "$prayerName in $minutesBefore minutes"
            }

            val body = if (minutesBefore == 0) {
                "It is now time for $prayerName prayer ($prayerTime). Hayya 'ala-s-Salah."
            } else {
                "$prayerName prayer begins at $prayerTime. Prepare for prayer."
            }

            val notification = NotificationCompat.Builder(context, PrayerReminderManager.CHANNEL_ID)
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

            NotificationManagerCompat.from(context).notify(prayerId, notification)

            // Reschedule upcoming prayer reminders
            PrayerReminderManager.scheduleReminders(context)
        }
    }
}
