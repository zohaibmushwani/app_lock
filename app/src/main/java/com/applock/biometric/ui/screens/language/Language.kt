package com.devsky.videorecorder.screens.language

import androidx.annotation.Keep
import kotlin.collections.find
import kotlin.collections.first

@Keep
data class Language(
    val code: String,
    val name: String,
    val flagCode: String,
    val isRTL: Boolean = false
) {
    companion object {
        val supportedLanguages = listOf(
            Language("en", "English", "us"),
            Language("es", "Español", "es"),
            Language("fr", "Français", "fr"),
            Language("de", "Deutsch", "de"),
            Language("it", "Italiano", "it"),
            Language("pt", "Português", "pt"),
            Language("ru", "Русский", "ru"),
            Language("zh", "中文", "cn"),
            Language("ja", "日本語", "jp"),
            Language("ko", "한국어", "kr"),
            Language("ar", "العربية", "sa", isRTL = true),
            Language("hi", "हिन्दी", "in")
        )
        
        fun getLanguageByCode(code: String): Language? {
            return supportedLanguages.find { it.code == code }
        }
        
        fun getDefaultLanguage(): Language {
            return supportedLanguages.first()
        }
    }
} 