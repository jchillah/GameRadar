package de.syntax_institut.androidabschlussprojekt.utils

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
                "" + itemName + ""

            items.isNullOrEmpty() ->
                "" + itemName + ""

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
            "-"
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