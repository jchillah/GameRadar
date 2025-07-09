package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import android.content.res.*
import android.os.*
import java.util.*

/**
 * LocaleManager für die Verwaltung der App-Sprache.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
object LocaleManager {

    private const val LANGUAGE_SYSTEM = "system"
    private const val LANGUAGE_GERMAN = "de"
    private const val LANGUAGE_ENGLISH = "en"
    // Französisch und Spanisch entfernt
    /**
     * Verfügbare Sprachen mit ihren Anzeigenamen.
     */
    val availableLanguages = mapOf(
        LANGUAGE_SYSTEM to "Systemsprache",
        LANGUAGE_GERMAN to "Deutsch",
        LANGUAGE_ENGLISH to "English"
    )

    /**
     * Erstellt einen Context mit der angegebenen Sprache.
     */
    fun createLocalizedContext(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            LANGUAGE_SYSTEM -> getSystemLocale(context)
            LANGUAGE_GERMAN -> Locale.GERMAN
            LANGUAGE_ENGLISH -> Locale.ENGLISH
            else -> getSystemLocale(context)
        }

        return updateResources(context, locale)
    }

    /**
     * Erhält die Systemsprache des Geräts.
     */
    private fun getSystemLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    /**
     * Aktualisiert die Ressourcen mit der neuen Locale.
     */
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }

        return context.createConfigurationContext(configuration)
    }

    /**
     * Erhält den Anzeigenamen für einen Sprachcode.
     */
    fun getLanguageDisplayName(languageCode: String): String {
        return availableLanguages[languageCode] ?: availableLanguages[LANGUAGE_SYSTEM]!!
    }

    /**
     * Prüft, ob eine Sprache die Systemsprache ist.
     */
    fun isSystemLanguage(languageCode: String): Boolean {
        return languageCode == LANGUAGE_SYSTEM
    }

    /**
     * Erhält den aktuellen Sprachcode basierend auf der Locale.
     */
    fun getCurrentLanguageCode(context: Context): String {
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        return when (currentLocale.language) {
            "de" -> LANGUAGE_GERMAN
            "en" -> LANGUAGE_ENGLISH
            else -> LANGUAGE_SYSTEM
        }
    }
} 