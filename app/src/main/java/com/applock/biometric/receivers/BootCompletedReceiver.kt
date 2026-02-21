package com.applock.biometric.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.applock.biometric.data.PreferencesManager
import com.applock.biometric.helpers.AuthSession
import com.applock.biometric.helpers.PermissionUtils
import com.applock.biometric.services.AppGuardForegroundService

/**
 * Receives BOOT_COMPLETED to ensure App Lock protection is active immediately after device restart.
 * - Clears auth session so all locked apps require authentication after reboot
 * - Starts foreground service when user has locked apps, to keep the process alive and reliable
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        // Initialize data layer (in case Application hasn't run yet in this process)
        PreferencesManager.init(context)

        // Clear auth session - after reboot, all locked apps must require fresh authentication
        AuthSession.clear()

        // Start foreground service only if user has locked apps and accessibility is enabled.
        // This keeps our process alive so the AccessibilityService can reliably intercept app launches.
        val lockedApps = PreferencesManager.getLockedApps()
        if (lockedApps.isNotEmpty() && PermissionUtils.isAccessibilityServiceEnabled(context)) {
            val serviceIntent = Intent(context, AppGuardForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
