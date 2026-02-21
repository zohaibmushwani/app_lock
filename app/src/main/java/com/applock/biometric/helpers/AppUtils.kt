package com.applock.biometric.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable
)


object AppUtils {

    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val apps = mutableListOf<AppInfo>()

        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in packages) {

            // Skip your own app
            if (app.packageName == context.packageName) continue

            // Only apps that appear in launcher
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent == null) continue

            try {
                val appName = pm.getApplicationLabel(app).toString()
                val icon = pm.getApplicationIcon(app.packageName)
                apps.add(AppInfo(app.packageName, appName, icon))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return apps.sortedBy { it.appName.lowercase() }
    }
}