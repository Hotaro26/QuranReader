package com.hotaro.mizan.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object AppIconManager {

    private val aliases = listOf(
        "emerald" to "com.hotaro.mizan.MainActivityEmerald",
        "midnight" to "com.hotaro.mizan.MainActivityMidnight",
        "purple" to "com.hotaro.mizan.MainActivityPurple"
    )

    fun setAppIcon(context: Context, iconKey: String) {
        try {
            val pm = context.packageManager
            val pkg = context.packageName

            for ((key, aliasClass) in aliases) {
                val component = ComponentName(pkg, aliasClass)
                val targetState = if (key == iconKey) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                pm.setComponentEnabledSetting(
                    component,
                    targetState,
                    PackageManager.DONT_KILL_APP
                )
            }
            Log.d("AppIconManager", "App icon switched to: $iconKey")
        } catch (e: Exception) {
            Log.e("AppIconManager", "Failed to switch app icon: ${e.message}", e)
        }
    }
}
