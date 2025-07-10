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
    /**
     * Verfügbare Sprachen mit ihren Anzeigenamen.
     */
    val availableLanguages = mapOf(
        LANGUAGE_SYSTEM to "Systemsprache",
        LANGUAGE_GERMAN to "Deutsch",
        LANGUAGE_ENGLISH to "English",
    )

    /**
     * Erstellt einen Context mit der angegebenen Sprache.
     */
    fun createLocalizedContext(context: Context, languageCode: String): Context {
        return when (languageCode) {
            LANGUAGE_SYSTEM -> context // System: Context unverändert zurückgeben
            LANGUAGE_GERMAN -> updateResources(context, Locale.GERMAN)
            LANGUAGE_ENGLISH -> updateResources(context, Locale.ENGLISH)
            else -> context
        }
    }

    /**
     * Erhält die Systemsprache des Geräts.
     */
    private fun getSystemLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }

    /**
     * Aktualisiert die Ressourcen mit der neuen Locale.
     */
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)

        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }
} 