package com.applock.biometric

import android.app.Application
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.data.PreferencesManager

class AppLockApp : Application() {
    override fun onCreate() {
        super.onCreate()

        SharedPrefsHelper.init(this)
        PreferencesManager.init(this)
    }
}