package de.syntax_institut.androidabschlussprojekt.utils

import de.syntax_institut.androidabschlussprojekt.data.*

/**
 * Utility-Klasse für häufig verwendete Funktionen.
 * Reduziert Code-Duplikation und verbessert Wartbarkeit.
 */
object CommonUtils {



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
                Constants.EMPTY_STATE_OFFLINE_PREFIX + itemName + Constants.EMPTY_STATE_OFFLINE_SUFFIX

            items.isNullOrEmpty() ->
                Constants.EMPTY_STATE_PREFIX + itemName + Constants.EMPTY_STATE_SUFFIX

            else -> ""
        }
    }

    /**
     * Formatiert eine Bewertung für die Anzeige.
     */
    fun formatRating(rating: Float): String {
        return if (rating > 0f) {
            String.format(java.util.Locale.getDefault(), "%.1f", rating)
        } else {
            Constants.NO_RATING
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