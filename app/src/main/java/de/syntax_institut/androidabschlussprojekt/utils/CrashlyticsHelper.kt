package de.syntax_institut.androidabschlussprojekt.utils

/**
 * Helper-Klasse für Firebase Crashlytics Integration..
 *
 * TEMPORÄR: Stub-Implementierung bis Crashlytics Plugin korrekt funktioniert.
 */
object CrashlyticsHelper {

    fun init() {
        try {
            AppLogger.d("Crashlytics", "Firebase Crashlytics Stub initialisiert")
        } catch (e: Exception) {
            AppLogger.e(
                "Crashlytics",
                "Crashlytics Stub konnte nicht initialisiert werden: ${e.localizedMessage}"
            )
        }
    }

    /** Setzt einen benutzerdefinierten Schlüssel-Wert-Paar. */
    fun setCustomKey(key: String, value: String) {
        AppLogger.d("Crashlytics", "Custom Key gesetzt: $key = $value")
    }

    /** Setzt einen benutzerdefinierten Schlüssel-Wert-Paar (Int). */
    fun setCustomKey(key: String, value: Int) {
        AppLogger.d("Crashlytics", "Custom Key gesetzt: $key = $value")
    }

    /** Setzt einen benutzerdefinierten Schlüssel-Wert-Paar (Boolean). */
    fun setCustomKey(key: String, value: Boolean) {
        AppLogger.d("Crashlytics", "Custom Key gesetzt: $key = $value")
    }

    /** Setzt eine benutzerdefinierte ID für den Benutzer. */
    fun setUserId(userId: String) {
        AppLogger.d("Crashlytics", "User ID gesetzt: $userId")
    }

    /** Loggt einen nicht-fatale Exception. */
    fun recordException(throwable: Throwable) {
        AppLogger.e("Crashlytics", "Exception aufgezeichnet: ${throwable.message}")
    }

    /** Loggt einen Fehler mit Kontext. */
    fun recordError(error: String, context: String) {
        AppLogger.e("Crashlytics", "Error aufgezeichnet: $context - $error")
    }

    /** Loggt einen API-Fehler. */
    fun recordApiError(endpoint: String, errorCode: Int, errorMessage: String) {
        AppLogger.e("Crashlytics", "API Error aufgezeichnet: $endpoint - $errorCode: $errorMessage")
    }

    /** Loggt einen Datenbank-Fehler. */
    fun recordDatabaseError(operation: String, table: String, error: String) {
        AppLogger.e("Crashlytics", "Database Error aufgezeichnet: $operation auf $table - $error")
    }

    /** Loggt einen Netzwerk-Fehler. */
    fun recordNetworkError(requestType: String, url: String, error: String) {
        AppLogger.e("Crashlytics", "Network Error aufgezeichnet: $requestType - $url - $error")
    }

    /** Loggt einen UI-Fehler. */
    fun recordUiError(screenName: String, component: String, error: String) {
        AppLogger.e("Crashlytics", "UI Error aufgezeichnet: $screenName - $component - $error")
    }

    /** Loggt einen Performance-Fehler. */
    fun recordPerformanceError(metricName: String, expectedValue: Long, actualValue: Long) {
        AppLogger.e(
            "Crashlytics",
            "Performance Error aufgezeichnet: $metricName - Expected: ${expectedValue}ms, Actual: ${actualValue}ms"
        )
    }

    /** Loggt einen Cache-Fehler. */
    fun recordCacheError(operation: String, cacheSize: Int, error: String) {
        AppLogger.e(
            "Crashlytics",
            "Cache Error aufgezeichnet: $operation - Size: $cacheSize - $error"
        )
    }

    /** Loggt einen Bildlade-Fehler. */
    fun recordImageLoadError(imageUrl: String, error: String) {
        AppLogger.e("Crashlytics", "Image Load Error aufgezeichnet: $imageUrl - $error")
    }

    /** Loggt einen Navigation-Fehler. */
    fun recordNavigationError(fromScreen: String, toScreen: String, error: String) {
        AppLogger.e(
            "Crashlytics",
            "Navigation Error aufgezeichnet: $fromScreen -> $toScreen - $error"
        )
    }

    /** Loggt einen Einstellungs-Fehler. */
    fun recordSettingsError(settingName: String, operation: String, error: String) {
        AppLogger.e(
            "Crashlytics",
            "Settings Error aufgezeichnet: $settingName - $operation - $error"
        )
    }

    /** Loggt einen Wishlist-Fehler. */
    fun recordWishlistError(operation: String, gameId: Int, error: String) {
        AppLogger.e(
            "Crashlytics",
            "Wishlist Error aufgezeichnet: $operation - Game ID: $gameId - $error"
        )
    }

    /** Loggt einen Favoriten-Fehler. */
    fun recordFavoriteError(operation: String, gameId: Int, error: String) {
        AppLogger.e(
            "Crashlytics",
            "Favorite Error aufgezeichnet: $operation - Game ID: $gameId - $error"
        )
    }

    /** Setzt App-spezifische Informationen. */
    fun setAppInfo() {
        AppLogger.d("Crashlytics", "App-Informationen gesetzt")
    }

    /** Aktiviert oder deaktiviert Crashlytics. */
    fun setCrashlyticsEnabled(enabled: Boolean) {
        AppLogger.d("Crashlytics", "Crashlytics ${if (enabled) "aktiviert" else "deaktiviert"}")

        // Analytics Event senden
        AppAnalytics.trackCrashlyticsEnabled(enabled)
    }
}
