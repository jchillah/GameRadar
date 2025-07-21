package de.syntax_institut.androidabschlussprojekt.data.local.models

/**
 * Datenklasse für Cache-Statistiken.
 * Enthält die Anzahl und die Gesamtgröße der gecachten Einträge.
 *
 * @property count Anzahl der gecachten Einträge
 * @property size Gesamtgröße des Caches in Bytes
 */
data class CacheStats(
    /** Anzahl der gecachten Einträge */
    val count: Int,
    /** Gesamtgröße des Caches in Bytes */
    val size: Long,
) 