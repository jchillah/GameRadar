package de.syntax_institut.androidabschlussprojekt.ui.states

import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * UI-State für den Settings-Screen.
 *
 * Kapselt alle UI-relevanten Zustände für die Einstellungen:
 * - Benutzer-Einstellungen (Notifications, Analytics, etc.)
 * - Pro-Status und Werbung
 * - Cache-Statistiken
 * - Loading- und Error-States
 *
 * Clean Code: Single Responsibility, DRY, KISS, KDoc.
 */
data class SettingsUiState(
    // Benutzer-Einstellungen
    val notificationsEnabled: Boolean = false,
    val autoRefreshEnabled: Boolean = true,
    val imageQuality: ImageQuality = ImageQuality.HIGH,
    val language: String = "system",
    val gamingModeEnabled: Boolean = false,
    val performanceModeEnabled: Boolean = false,
    val shareGamesEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,

    // Werbung
    val adsEnabled: Boolean = true,
    val analyticsEnabled: Boolean = true,

    // Cache-Statistiken
    val cacheSize: Int = 0,
    val maxCacheSize: Int = 0,
    val lastSyncTime: Long? = null,

    // UI-States
    val isLoading: Boolean = false,
    val error: String? = null,
) {

    /** Gibt an, ob der Cache voll ist (> 70% Nutzung). */
    val isCacheFull: Boolean
        get() = maxCacheSize > 0 && (cacheSize.toFloat() / maxCacheSize) > 0.7f

    /** Gibt die Cache-Nutzung als Prozentsatz zurück. */
    val cacheUsagePercentage: Int
        get() =
            if (maxCacheSize > 0) {
                ((cacheSize.toFloat() / maxCacheSize) * 100).toInt()
            } else 0
}
