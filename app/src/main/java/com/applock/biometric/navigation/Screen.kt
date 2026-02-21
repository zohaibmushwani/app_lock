package com.applock.biometric.navigation

import androidx.annotation.Keep
import com.applock.biometric.R

@Keep
sealed class Screen(
    val route: String,
    val titleResId: Int,
    val icon: Any? = null
) {
    object Splash : Screen("splash", R.string.splash)
    object Walkthrough : Screen("walkthrough", R.string.walkthrough)
    object Home : Screen("home", R.string.home)

    object Settings : Screen("settings", R.string.settings)
//    object Videos : Screen("videos", R.string.videos)
    object LanguageSelection : Screen("languageSelection", R.string.language_selection)
    object AboutApp : Screen("aboutApp", R.string.about_app)
    object Permissions : Screen("permissions", R.string.permissions_required)
    object BatteryOptimization : Screen("battery_optimization", R.string.disable_battery_optimization)


    object PinEntry : Screen("pin_entry", R.string.pin_entry)
    object PinSetup : Screen("pin_setup", R.string.pin_setup)

    object SecurityQuestion : Screen("security_question_screen", R.string.security_question_title)
    object PasswordRecovery : Screen("password_recovery_screen", R.string.password_recovery)
    object SecuritySettings : Screen("security_settings_screen", R.string.security_settings)

    object BgRecorder : Screen("bg_recorder", R.string.bg_video_recording)

}
