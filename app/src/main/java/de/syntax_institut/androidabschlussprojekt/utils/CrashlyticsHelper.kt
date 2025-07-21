package de.syntax_institut.androidabschlussprojekt.utils

import com.google.firebase.crashlytics.*

/**
 * Helper-Klasse für Firebase Crashlytics Integration.
 *
 * Bietet eine zentrale Schnittstelle für:
 * - Initialisierung und Konfiguration
 * - Custom Keys und User ID setzen
 * - Exception und Error Recording
 * - Performance und Analytics Integration
 * - Spezifische Error-Typen für verschiedene Bereiche
 *
 * Clean Code: Single Responsibility, DRY, KISS, KDoc.
 */
object CrashlyticsHelper {

    private var isInitialized = false
    private var isEnabled = true

    /** Initialisiert Firebase Crashlytics. Sollte in der Application-Klasse aufgerufen werden. */
    fun init() {
        try {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
            isInitialized = true
            isEnabled = true
            AppLogger.d("CrashlyticsHelper", "Firebase Crashlytics initialisiert")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler bei Crashlytics-Initialisierung", e)
            isEnabled = false
        }
    }

    /**
     * Setzt App-spezifische Informationen für Crashlytics. Wird automatisch bei der Initialisierung
     * aufgerufen.
     */
    fun setAppInfo() {
        if (!isEnabled) return
        try {
            setCustomKey("app_name", "GameRadar")
            setCustomKey("app_package", "de.syntax_institut.androidabschlussprojekt")
            setCustomKey("crashlytics_initialized", true)
            AppLogger.d("CrashlyticsHelper", "App-Informationen gesetzt")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen der App-Informationen", e)
        }
    }

    /**
     * Aktiviert oder deaktiviert Crashlytics.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setCrashlyticsEnabled(enabled: Boolean) {
        try {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = enabled
            isEnabled = enabled
            AppLogger.d("CrashlyticsHelper", "Crashlytics enabled: $enabled")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen von Crashlytics enabled", e)
        }
    }

    /**
     * Setzt die User ID für Crashlytics.
     * @param userId Die eindeutige User ID
     */
    fun setUserId(userId: String) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setUserId(userId)
            AppLogger.d("CrashlyticsHelper", "User ID gesetzt: $userId")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen der User ID", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics.
     * @param key Der Schlüssel
     * @param value Der Wert
     */
    fun setCustomKey(key: String, value: String) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics (Boolean).
     * @param key Der Schlüssel
     * @param value Der Boolean-Wert
     */
    fun setCustomKey(key: String, value: Boolean) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics (Int).
     * @param key Der Schlüssel
     * @param value Der Integer-Wert
     */
    fun setCustomKey(key: String, value: Int) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics (Long).
     * @param key Der Schlüssel
     * @param value Der Long-Wert
     */
    fun setCustomKey(key: String, value: Long) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics (Float).
     * @param key Der Schlüssel
     * @param value Der Float-Wert
     */
    fun setCustomKey(key: String, value: Float) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Setzt einen Custom Key für Crashlytics (Double).
     * @param key Der Schlüssel
     * @param value Der Double-Wert
     */
    fun setCustomKey(key: String, value: Double) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
            AppLogger.d("CrashlyticsHelper", "Custom Key gesetzt: $key = $value")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Setzen des Custom Keys", e)
        }
    }

    /**
     * Zeichnet eine Exception in Crashlytics auf.
     * @param throwable Die zu loggende Exception
     */
    fun recordException(throwable: Throwable) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            AppLogger.e("CrashlyticsHelper", "Exception in Crashlytics aufgezeichnet", throwable)
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen der Exception", e)
        }
    }

    /**
     * Zeichnet eine Exception in Crashlytics auf.
     * @param throwable Die zu loggende Exception
     */
    fun recordError(throwable: Throwable) {
        recordException(throwable)
    }

    /**
     * Zeichnet einen API-Fehler in Crashlytics auf.
     * @param endpoint Der API-Endpoint
     * @param errorCode Der HTTP-Statuscode
     * @param errorMessage Die Fehlermeldung
     */
    fun recordApiError(endpoint: String, errorCode: Int, errorMessage: String) {
        if (!isEnabled) return
        try {
            setCustomKey("api_error_endpoint", endpoint)
            setCustomKey("api_error_code", errorCode)
            setCustomKey("api_error_message", errorMessage)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("API Error: $endpoint - $errorCode: $errorMessage"))
            AppLogger.e(
                "CrashlyticsHelper",
                "API Error aufgezeichnet: $endpoint - $errorCode: $errorMessage"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des API-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Netzwerk-Fehler in Crashlytics auf.
     * @param requestType Der Request-Typ (GET, POST, etc.)
     * @param url Die URL
     * @param error Die Fehlermeldung
     */
    fun recordNetworkError(requestType: String, url: String, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("network_error_type", requestType)
            setCustomKey("network_error_url", url)
            setCustomKey("network_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("Network Error: $requestType $url - $error"))
            AppLogger.e(
                "CrashlyticsHelper",
                "Network Error aufgezeichnet: $requestType $url - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Netzwerk-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen UI-Fehler in Crashlytics auf.
     * @param screenName Der Name des Screens
     * @param componentName Der Name der Komponente
     * @param error Die Fehlermeldung
     */
    fun recordUiError(screenName: String, componentName: String, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("ui_error_screen", screenName)
            setCustomKey("ui_error_component", componentName)
            setCustomKey("ui_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("UI Error: $screenName/$componentName - $error"))
            AppLogger.e(
                "CrashlyticsHelper",
                "UI Error aufgezeichnet: $screenName/$componentName - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des UI-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Performance-Fehler in Crashlytics auf.
     * @param operation Die Operation
     * @param duration Die Dauer in Millisekunden
     * @param threshold Der Schwellenwert
     */
    fun recordPerformanceError(operation: String, duration: Long, threshold: Long) {
        if (!isEnabled) return
        try {
            setCustomKey("performance_error_operation", operation)
            setCustomKey("performance_error_duration", duration)
            setCustomKey("performance_error_threshold", threshold)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception(
                        "Performance Error: $operation took ${duration}ms (threshold: ${threshold}ms)"
                    )
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Performance Error aufgezeichnet: $operation took ${duration}ms (threshold: ${threshold}ms)"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Performance-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Cache-Fehler in Crashlytics auf.
     * @param operation Die Cache-Operation
     * @param cacheSize Die Cache-Größe
     * @param error Die Fehlermeldung
     */
    fun recordCacheError(operation: String, cacheSize: Int, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("cache_error_operation", operation)
            setCustomKey("cache_error_size", cacheSize)
            setCustomKey("cache_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception("Cache Error: $operation (size: $cacheSize) - $error")
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Cache Error aufgezeichnet: $operation (size: $cacheSize) - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Cache-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Bildlade-Fehler in Crashlytics auf.
     * @param imageUrl Die Bild-URL
     * @param error Die Fehlermeldung
     */
    fun recordImageLoadError(imageUrl: String, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("image_load_error_url", imageUrl)
            setCustomKey("image_load_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("Image Load Error: $imageUrl - $error"))
            AppLogger.e("CrashlyticsHelper", "Image Load Error aufgezeichnet: $imageUrl - $error")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Bildlade-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Navigations-Fehler in Crashlytics auf.
     * @param fromScreen Der Ausgangs-Screen
     * @param toScreen Der Ziel-Screen
     * @param error Die Fehlermeldung
     */
    fun recordNavigationError(fromScreen: String, toScreen: String, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("navigation_error_from", fromScreen)
            setCustomKey("navigation_error_to", toScreen)
            setCustomKey("navigation_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception("Navigation Error: $fromScreen -> $toScreen - $error")
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Navigation Error aufgezeichnet: $fromScreen -> $toScreen - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Navigations-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Settings-Fehler in Crashlytics auf.
     * @param settingName Der Name der Einstellung
     * @param oldValue Der alte Wert
     * @param newValue Der neue Wert
     * @param error Die Fehlermeldung
     */
    fun recordSettingsError(
        settingName: String,
        oldValue: String,
        newValue: String,
        error: String,
    ) {
        if (!isEnabled) return
        try {
            setCustomKey("settings_error_name", settingName)
            setCustomKey("settings_error_old_value", oldValue)
            setCustomKey("settings_error_new_value", newValue)
            setCustomKey("settings_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception(
                        "Settings Error: $settingName ($oldValue -> $newValue) - $error"
                    )
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Settings Error aufgezeichnet: $settingName ($oldValue -> $newValue) - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Settings-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Datenbank-Fehler in Crashlytics auf.
     * @param operation Die Datenbank-Operation
     * @param table Die betroffene Tabelle
     * @param error Die Fehlermeldung
     */
    fun recordDatabaseError(operation: String, table: String, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("database_error_operation", operation)
            setCustomKey("database_error_table", table)
            setCustomKey("database_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(Exception("Database Error: $operation on $table - $error"))
            AppLogger.e(
                "CrashlyticsHelper",
                "Database Error aufgezeichnet: $operation on $table - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Datenbank-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Favoriten-Fehler in Crashlytics auf.
     * @param operation Die Favoriten-Operation
     * @param gameId Die Spiel-ID
     * @param error Die Fehlermeldung
     */
    fun recordFavoriteError(operation: String, gameId: Int, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("favorite_error_operation", operation)
            setCustomKey("favorite_error_game_id", gameId)
            setCustomKey("favorite_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception("Favorite Error: $operation for game $gameId - $error")
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Favorite Error aufgezeichnet: $operation for game $gameId - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Favoriten-Fehlers", e)
        }
    }

    /**
     * Zeichnet einen Wunschlisten-Fehler in Crashlytics auf.
     * @param operation Die Wunschlisten-Operation
     * @param gameId Die Spiel-ID
     * @param error Die Fehlermeldung
     */
    fun recordWishlistError(operation: String, gameId: Int, error: String) {
        if (!isEnabled) return
        try {
            setCustomKey("wishlist_error_operation", operation)
            setCustomKey("wishlist_error_game_id", gameId)
            setCustomKey("wishlist_error_message", error)
            FirebaseCrashlytics.getInstance()
                .recordException(
                    Exception("Wishlist Error: $operation for game $gameId - $error")
                )
            AppLogger.e(
                "CrashlyticsHelper",
                "Wishlist Error aufgezeichnet: $operation for game $gameId - $error"
            )
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Aufzeichnen des Wunschlisten-Fehlers", e)
        }
    }

    /**
     * Loggt eine Nachricht in Crashlytics.
     * @param message Die zu loggende Nachricht
     */
    fun log(message: String) {
        if (!isEnabled) return
        try {
            FirebaseCrashlytics.getInstance().log(message)
            AppLogger.d("CrashlyticsHelper", "Log in Crashlytics: $message")
        } catch (e: Exception) {
            AppLogger.e("CrashlyticsHelper", "Fehler beim Loggen in Crashlytics", e)
        }
    }

    /**
     * Gibt an, ob Crashlytics aktiviert ist.
     * @return true wenn aktiviert, false sonst
     */
    fun isEnabled(): Boolean = isEnabled

    /**
     * Gibt an, ob Crashlytics initialisiert wurde.
     * @return true wenn initialisiert, false sonst
     */
    fun isInitialized(): Boolean = isInitialized
}
