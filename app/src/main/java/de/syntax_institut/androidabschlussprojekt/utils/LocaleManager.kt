package de.syntax_institut.androidabschlussprojekt.utils

import android.content.*
import android.content.res.*
import android.os.*
import java.util.*

/**
 * LocaleManager für die Verwaltung der App-Sprache. Folgt Clean Code Best Practices: Single
 * Responsibility, DRY, KISS.
 */
object LocaleManager {

    private const val LANGUAGE_SYSTEM = "system"
    private const val LANGUAGE_GERMAN = "de"
    private const val LANGUAGE_ENGLISH = "en"

    /** Verfügbare Sprachen mit ihren Anzeigenamen. */
    val availableLanguages =
        mapOf(
            LANGUAGE_SYSTEM to "Systemsprache",
            LANGUAGE_GERMAN to "Deutsch",
            LANGUAGE_ENGLISH to "English",
        )

    /** Erstellt einen Context mit der angegebenen Sprache. */
    fun createLocalizedContext(context: Context, languageCode: String): Context {
        return when (languageCode) {
            LANGUAGE_SYSTEM -> {
                // System: Verwende die Systemsprache
                val systemLocale = getSystemLocale(context)
                AppLogger.d(
                    "LocaleManager",
                    "Erstelle Context für Systemsprache: ${systemLocale.language}"
                )
                updateResources(context, systemLocale)
            }

            LANGUAGE_GERMAN -> {
                AppLogger.d("LocaleManager", "Erstelle Context für Deutsch")
                updateResources(context, Locale.GERMAN)
            }

            LANGUAGE_ENGLISH -> {
                AppLogger.d("LocaleManager", "Erstelle Context für Englisch")
                updateResources(context, Locale.ENGLISH)
            }

            else -> {
                AppLogger.w("LocaleManager", "Unbekannte Sprache: $languageCode, verwende Standard")
                context
            }
        }
    }

    /** Erhält die Systemsprache des Geräts. */
    fun getSystemLocale(context: Context): Locale {
        return context.resources.configuration.locales[0]
    }

    /** Gibt die verfügbaren Sprachen für die UI zurück. */
    fun getAvailableLanguagesForUI(): Map<String, String> {
        return availableLanguages
    }

    /** Prüft, ob eine Sprache unterstützt wird. */
    fun isLanguageSupported(languageCode: String): Boolean {
        return availableLanguages.containsKey(languageCode)
    }

    /** Aktualisiert die Ressourcen mit der neuen Locale. */
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocales(LocaleList(locale))

        val localizedContext = context.createConfigurationContext(configuration)
        AppLogger.d("LocaleManager", "Context erstellt für Locale: ${locale.language}")

        return localizedContext
    }
}
