package de.syntax_institut.androidabschlussprojekt.utils

import android.util.*
import kotlinx.coroutines.*

/**
 * Utility-Klasse für häufig verwendete Funktionen.
 * Reduziert Code-Duplikation und verbessert Wartbarkeit.
 */
object CommonUtils {

    /**
     * Führt eine Operation mit Logging und Error-Handling aus.
     *
     * @param tag Log-Tag für die Operation
     * @param operationName Name der Operation für Logging
     * @param scope CoroutineScope für die Ausführung
     * @param operation Die auszuführende Operation
     * @param onError Callback bei Fehlern
     */
    inline fun <T> executeWithLogging(
        tag: String,
        operationName: String,
        scope: CoroutineScope,
        crossinline operation: suspend () -> T,
        crossinline onError: (Exception) -> Unit = { },
    ) {
        Log.d(tag, "[DEBUG] $operationName gestartet")
        scope.launch {
            try {
                val result = operation()
                Log.d(tag, "[DEBUG] $operationName erfolgreich abgeschlossen")
            } catch (e: Exception) {
                Log.e(tag, "[ERROR] $operationName fehlgeschlagen", e)
                onError(e)
            }
        }
    }

    /**
     * Prüft ob eine Liste nicht leer ist und gibt eine benutzerfreundliche Nachricht zurück.
     */
    fun getEmptyStateMessage(
        items: List<*>?,
        itemName: String,
        isOffline: Boolean = false,
    ): String {
        return when {
            items.isNullOrEmpty() && isOffline ->
                "Keine $itemName verfügbar. Prüfe deine Internetverbindung und versuche es erneut."

            items.isNullOrEmpty() ->
                "Für dieses Spiel wurden keine $itemName gefunden."

            else -> ""
        }
    }

    /**
     * Formatiert eine Bewertung für die Anzeige.
     */
    fun formatRating(rating: Float): String {
        return if (rating > 0f) {
            String.format("%.1f", rating)
        } else {
            "Keine Bewertung"
        }
    }

    /**
     * Prüft ob eine URL gültig ist.
     */
    fun isValidUrl(url: String?): Boolean {
        return !url.isNullOrBlank() &&
                (url.startsWith("http://") || url.startsWith("https://"))
    }
} 