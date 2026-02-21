package com.applock.biometric.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.applock.biometric.activities.LockActivity
import com.applock.biometric.data.PreferencesManager
import com.applock.biometric.helpers.AuthSession

class AppLockService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        PreferencesManager.init(this)
        // Optional: Perform any setup here
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            // Critical Loop Prevention: Do not lock own app components explicitly here
            // (Though LockActivity should ideally be excluded from locked list anyway)
            if (packageName == this.packageName) return 

            if (PreferencesManager.isAppLocked(packageName)) {
                android.util.Log.d("AppLockService", "App detected: $packageName. Locked: true")
                if (!AuthSession.isSessionValid(packageName)) {
                    android.util.Log.d("AppLockService", "Session invalid. Showing lock screen.")
                    showLockScreen(packageName)
                } else {
                    android.util.Log.d("AppLockService", "Session valid. Skipping lock screen.")
                }
            } else {
                 // android.util.Log.d("AppLockService", "App detected: $packageName. Locked: false")
            }
        }
    }

    private fun showLockScreen(targetPackage: String) {
        if (!android.provider.Settings.canDrawOverlays(this)) {
            android.util.Log.e("AppLockService", "Overlay permission missing!")
            return
        }

        val intent = Intent(this, LockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            putExtra("TARGET_PACKAGE", targetPackage)
        }
        try {
            startActivity(intent)
            android.util.Log.d("AppLockService", "LockActivity started for $targetPackage")
        } catch (e: Exception) {
            android.util.Log.e("AppLockService", "Failed to start LockActivity", e)
        }
    }

    override fun onInterrupt() {
        // Handle interruption (optional)
    }
}
