package com.applock.biometric.helpers

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import com.applock.biometric.services.AppLockService

object PermissionUtils {

    /**
     * Checks if our AppLock accessibility service is enabled.
     * Uses AccessibilityManager (official API) first for reliability across Android versions.
     * Falls back to Settings.Secure for edge cases.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        // Method 1: Use official AccessibilityManager API - most reliable across all Android versions
        try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
                ?: return fallbackAccessibilityCheck(context)
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            val expectedPackage = context.packageName
            val expectedClassName = AppLockService::class.java.name
            val expectedShortName = AppLockService::class.java.simpleName
            val isEnabled = enabledServices.any { serviceInfo ->
                serviceInfo.resolveInfo?.serviceInfo?.let { info ->
                    info.packageName == expectedPackage &&
                        (info.name == expectedClassName || info.name == expectedShortName ||
                            info.name?.endsWith(expectedShortName) == true)
                } ?: false
            }
            if (isEnabled) return true
        } catch (e: Exception) {
            // Fall through to fallback
        }

        return fallbackAccessibilityCheck(context)
    }

    /**
     * Fallback: read Settings.Secure for older devices or if AccessibilityManager doesn't find it.
     * Handles both ':' and '/' delimiters used in ENABLED_ACCESSIBILITY_SERVICES.
     */
    private fun fallbackAccessibilityCheck(context: Context): Boolean {
        val expectedComponent = ComponentName(context, AppLockService::class.java)
        val expectedFlat = expectedComponent.flattenToString()
        val enabledSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        // Try colon first (standard format), then slash (some OEMs)
        for (delimiter in listOf(':', '/')) {
            val splitter = TextUtils.SimpleStringSplitter(delimiter)
            splitter.setString(enabledSetting)
            while (splitter.hasNext()) {
                val part = splitter.next().trim()
                if (part.isEmpty()) continue
                val component = ComponentName.unflattenFromString(part)
                if (component != null && component == expectedComponent) return true
                if (part == expectedFlat) return true
            }
        }
        return false
    }

    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Pre-M usually granted by manifest
        }
    }

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }
    fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        activityManager?.getRunningServices(Int.MAX_VALUE)?.forEach { service ->
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    fun isAdminActive(context: Context): Boolean {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        val componentName = ComponentName(context, com.applock.biometric.receivers.AppLockDeviceAdminReceiver::class.java)
        return devicePolicyManager.isAdminActive(componentName)
    }
}
