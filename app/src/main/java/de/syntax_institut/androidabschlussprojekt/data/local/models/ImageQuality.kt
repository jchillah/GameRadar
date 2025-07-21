package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Enum für die Bildqualitätsstufen von Vorschaubildern und Trailern.
 * Dient zur Auswahl der gewünschten Bildqualität in der App.
 *
 * @property displayName Anzeigename der Qualitätsstufe
 */
enum class ImageQuality(val displayName: String) {
    /** Niedrige Qualität (640x480) */
    LOW("Niedrig"),

    /** Mittlere Qualität (1024x768) */
    MEDIUM("Mittel"),

    /** Hohe Qualität (höchste verfügbare Auflösung) */
    HIGH("Hoch");

    /**
     * Liefert den Anzeigenamen der Qualitätsstufe.
     */
    val title: String
        get() = displayName

    companion object {
        /**
         * Gibt die ImageQuality-Instanz zum Anzeigenamen zurück.
         * @param displayName Der Anzeigename
         * @return Die passende ImageQuality oder HIGH als Fallback
         */
        fun fromDisplayName(displayName: String): ImageQuality {
            return entries.find { it.displayName == displayName } ?: HIGH
        }
    }
} 