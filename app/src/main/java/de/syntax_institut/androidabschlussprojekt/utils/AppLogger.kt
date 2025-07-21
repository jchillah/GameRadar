package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*
import de.syntax_institut.androidabschlussprojekt.*

/**
 * Zentrale Logging-Utility für die gesamte App.
 *
 * Features:
 * - Einheitliche Logging-Struktur mit globalem Tag
 * - Vier Log-Level: DEBUG, INFO, WARN, ERROR
 * - Debug-Modus-abhängige Aktivierung
 * - Automatische Tag-Präfixierung
 * - Exception-Handling für Error-Logs
 * - Performance-optimiert (keine Logs im Release-Modus)
 *
 * Verwendung:
 * - AppLogger.d("Tag", "Debug-Nachricht")
 * - AppLogger.i("Tag", "Info-Nachricht")
 * - AppLogger.w("Tag", "Warnung")
 * - AppLogger.e("Tag", "Fehler", exception)
 *
 * Log-Level:
 * - DEBUG: Entwicklungs- und Debugging-Informationen
 * - INFO: Allgemeine App-Informationen
 * - WARN: Warnungen und potenzielle Probleme
 * - ERROR: Fehler und Exceptions
 */
object AppLogger {
    private const val GLOBAL_TAG = "GameRadar"

    /**
     * Verfügbare Log-Level für strukturiertes Logging.
     */
    enum class Level { DEBUG, INFO, WARN, ERROR }

    /**
     * Gibt an, ob Logging aktiviert ist (nur im Debug-Modus).
     */
    var isLoggingEnabled: Boolean = BuildConfig.DEBUG

    /**
     * Debug-Log mit Tag und Nachricht.
     * @param tag Log-Tag für Kategorisierung
     * @param message Debug-Nachricht
     */
    fun d(tag: String, message: String) = log(Level.DEBUG, tag, message)

    /**
     * Info-Log mit Tag und Nachricht.
     * @param tag Log-Tag für Kategorisierung
     * @param message Info-Nachricht
     */
    fun i(tag: String, message: String) = log(Level.INFO, tag, message)

    /**
     * Warning-Log mit Tag und Nachricht.
     * @param tag Log-Tag für Kategorisierung
     * @param message Warnungsnachricht
     */
    fun w(tag: String, message: String) = log(Level.WARN, tag, message)

    /**
     * Error-Log mit Tag, Nachricht und optionaler Exception.
     * @param tag Log-Tag für Kategorisierung
     * @param message Fehlernachricht
     * @param throwable Optionale Exception für Stacktrace
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(Level.ERROR, tag, message, throwable)

    /**
     * Interne Logging-Funktion mit Level-Unterstützung.
     * @param level Log-Level (DEBUG, INFO, WARN, ERROR)
     * @param tag Log-Tag für Kategorisierung
     * @param message Log-Nachricht
     * @param throwable Optionale Exception für Error-Logs
     */
    private fun log(level: Level, tag: String, message: String, throwable: Throwable? = null) {
        if (!isLoggingEnabled) return
        val fullTag = "$GLOBAL_TAG-$tag"
        when (level) {
            Level.DEBUG -> Log.d(fullTag, message)
            Level.INFO -> Log.i(fullTag, message)
            Level.WARN -> Log.w(fullTag, message)
            Level.ERROR -> Log.e(fullTag, message, throwable)
        }
    }
} 