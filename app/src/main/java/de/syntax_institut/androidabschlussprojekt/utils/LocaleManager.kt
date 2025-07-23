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
        AppLogger.d("LocaleManager", "createLocalizedContext called with language: $languageCode")

        val locale = when (languageCode) {
            LANGUAGE_SYSTEM -> {
                // System: Verwende die Systemsprache
                val systemLocale = getSystemLocale(context)
                AppLogger.d("LocaleManager", "Verwende Systemsprache: ${systemLocale.language}")
                systemLocale
            }
            LANGUAGE_GERMAN -> {
                AppLogger.d("LocaleManager", "Verwende Deutsch")
                Locale.GERMAN
            }
            LANGUAGE_ENGLISH -> {
                AppLogger.d("LocaleManager", "Verwende Englisch")
                Locale.ENGLISH
            }
            else -> {
                AppLogger.w("LocaleManager", "Unbekannte Sprache: $languageCode, verwende Standard")
                return context
            }
        }

        return updateResources(context, locale)
    }

    /** Erhält die Systemsprache des Geräts. */
    fun getSystemLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    /** Gibt die verfügbaren Sprachen für die UI zurück. */
    fun getAvailableLanguagesForUI(): Map<String, String> {
        return availableLanguages
    }

    /** Prüft, ob eine Sprache unterstützt wird. */
    fun isLanguageSupported(languageCode: String): Boolean {
        return availableLanguages.containsKey(languageCode)
    }

    /**
     * Aktualisiert die Ressourcen mit der neuen Locale.
     * Diese Methode stellt sicher, dass die Sprachänderung sofort wirksam wird.
     */
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(configuration, locale)
            context.createConfigurationContext(configuration)
        } else {
            setSystemLocaleLegacy(configuration, locale)
            resources.updateConfiguration(
                configuration,
                resources.displayMetrics
            )
            context
        }.also {
            AppLogger.d("LocaleManager", "Locale aktualisiert auf: ${locale.language}")
        }
    }

    @Suppress("DEPRECATION")
    private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }

    private fun setSystemLocale(config: Configuration, locale: Locale) {
        config.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        config.setLocales(localeList)
    }
}
