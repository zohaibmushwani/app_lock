package com.applock.biometric.helpers

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.LocaleListCompat
import com.applock.biometric.common.SharedPrefsHelper
import java.util.Locale


@Keep
object LocaleHelper {

    fun getLayoutDirectionForLanguage(languageCode: String): LayoutDirection {
        return when (languageCode) {
            "ar", "he", "fa", "ur" -> LayoutDirection.Rtl
            else -> LayoutDirection.Ltr
        }
    }

    private fun persistLanguage(language: String) {
        SharedPrefsHelper.saveSelectedLanguage(language)
    }
    fun changeLanguageWithoutRestart(context: Context, languageCode: String): Context {
        persistLanguage(languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
        } else {
            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }
    fun changeLang(context: Context, languageCode: String): Context {
        persistLanguage(languageCode)

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            configuration.locale = locale
        } else {
            configuration.setLocales(LocaleList(locale))
        }

        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        val updatedContext = context.createConfigurationContext(configuration)

        try {
            val appContext = context.applicationContext
            appContext.resources.updateConfiguration(configuration, appContext.resources.displayMetrics)
        } catch (e: Exception) {
            Log.e("LocaleHelper", "Failed to update application context", e)
        }

        return updatedContext
    }

    fun changeLanguage(context: Context, languageCode: String) {
        persistLanguage(languageCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }

    fun getLanguageCode(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]?.toLanguageTag()?.split("-")?.first() ?: "en"
        } else {
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()?.split("-")?.first() ?: "en"
        }
    }

    fun getCountryFromNetwork(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (tm != null) {
            val countryCode = tm.networkCountryIso
            if (countryCode != null && !countryCode.isEmpty()) {
                return countryCode.uppercase(Locale.getDefault())
            }
        }
        return null
    }

}