package com.applock.biometric.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferencesManager {
    private const val PREF_NAME = "applock_prefs"
    private const val KEY_LOCKED_PACKAGES = "locked_packages"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isAppLocked(packageName: String): Boolean {
        if (!::prefs.isInitialized) return false
        val lockedPackages = prefs.getStringSet(KEY_LOCKED_PACKAGES, emptySet()) ?: emptySet()
        return lockedPackages.contains(packageName)
    }

    fun setAppLocked(packageName: String, isLocked: Boolean) {
        if (!::prefs.isInitialized) return
        val lockedPackages = prefs.getStringSet(KEY_LOCKED_PACKAGES, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (isLocked) {
            lockedPackages.add(packageName)
        } else {
            lockedPackages.remove(packageName)
        }
        prefs.edit { putStringSet(KEY_LOCKED_PACKAGES, lockedPackages) }
    }

    fun getLockedApps(): Set<String> {
        if (!::prefs.isInitialized) return emptySet()
        return prefs.getStringSet(KEY_LOCKED_PACKAGES, emptySet()) ?: emptySet()
    }
}
