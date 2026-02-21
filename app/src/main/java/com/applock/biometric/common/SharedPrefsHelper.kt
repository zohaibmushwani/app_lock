package com.applock.biometric.common

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SharedPrefsHelper {
    const val PREFS_NAME = "user_prefs"
    private const val KEY_RATING_DIALOG = "rating_dialog"
    private const val WALKTHROUGH_COMPLETED = "walkthrough_completed"
    private const val SELECTED_LANGUAGE = "selected_language"
    private const val LANGUAGE_SELECTION_COMPLETED = "language_selection_completed"
    private const val DISCLAIMER_ACKNOWLEDGED = "disclaimer_acknowledged"

    const val KEY_IS_AUTH = "isAuthenticated"
    const val KEY_APP_PASSWORD = "app_password"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isInitialized(): Boolean {
        return ::sharedPreferences.isInitialized
    }

    private fun ensureInitialized() {
        check(::sharedPreferences.isInitialized) { "SharedPrefsHelper must be initialized. Call init(context) first." }
    }

    fun saveBoolean(key: String, value: Boolean, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        ensureInitialized()
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String): String {
        ensureInitialized()
        val value = sharedPreferences.getString(key, defaultValue) ?: defaultValue
        return value
    }

    fun saveInt(key: String, value: Int, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        ensureInitialized()
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveSelectedLanguage(languageCode: String) {
        saveString(SELECTED_LANGUAGE, languageCode, true) // Force commit for language changes
    }

    fun getSelectedLanguage(): String {
        val language = getString(SELECTED_LANGUAGE, "en") // Default to English
        return language
    }

    fun isLanguageSelectionCompleted(): Boolean {
        return getBoolean(LANGUAGE_SELECTION_COMPLETED, false)
    }

    fun setLanguageSelectionCompleted(completed: Boolean) {
        saveBoolean(LANGUAGE_SELECTION_COMPLETED, completed)
    }

    fun saveLong(key: String, value: Long, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        ensureInitialized()
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun saveFloat(key: String, value: Float, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { putFloat(key, value) }
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        ensureInitialized()
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun remove(key: String, commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { remove(key) }
    }

    fun clear(commit: Boolean = false) {
        ensureInitialized()
        sharedPreferences.edit(commit) { clear() }
    }


    fun isWalkthroughCompleted(coroutineScope: CoroutineScope, callback: (Boolean) -> Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                getBoolean(WALKTHROUGH_COMPLETED, false)
            }
            callback(result)
        }
    }

    fun setWalkthroughCompleted() {
        saveBoolean(WALKTHROUGH_COMPLETED, true)
    }

    fun isFirstLaunch(): Boolean {
        return !isLanguageSelectionCompleted() && !getBoolean(WALKTHROUGH_COMPLETED, false)
    }

    fun isAppSetupComplete(): Boolean {
        return isLanguageSelectionCompleted() && getBoolean(WALKTHROUGH_COMPLETED, false)
    }


    fun isDisclaimerAcknowledged(): Boolean {
        return getBoolean(DISCLAIMER_ACKNOWLEDGED, false)
    }

    fun setDisclaimerAcknowledged(acknowledged: Boolean) {
        saveBoolean(DISCLAIMER_ACKNOWLEDGED, acknowledged)
    }

    fun isPinSet(): Boolean {
        return getString(KEY_APP_PASSWORD, "").isNotEmpty()
    }

    fun setPin(pin: String) {
        saveString(KEY_APP_PASSWORD, pin, true)
    }

    fun clearPin() {
        remove(KEY_APP_PASSWORD, true)
        saveBoolean(KEY_IS_AUTH, false, true)
    }

    fun isAuthenticated(): Boolean {
        return getBoolean(KEY_IS_AUTH, false)
    }

    fun isBiometricEnabled(): Boolean {
        return getBoolean("biometric_enabled", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        saveBoolean("biometric_enabled", enabled, true)
    }

    fun saveSecurityQuestion(question: String, answer: String) {
        ensureInitialized()
        sharedPreferences.edit(true) {
            putString("security_question", question)
            putString("security_answer", answer)
        }
    }

    fun getSecurityQuestion(): String? {
        ensureInitialized()
        return sharedPreferences.getString("security_question", null)
    }

    fun getSecurityAnswer(): String? {
        ensureInitialized()
        return sharedPreferences.getString("security_answer", null)
    }

    private inline fun SharedPreferences.edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = edit()
        action(editor)
        if (commit) editor.commit() else editor.apply()
    }
}