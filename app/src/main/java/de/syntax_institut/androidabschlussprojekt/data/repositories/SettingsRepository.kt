package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import androidx.core.content.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*

/**
 * Repository zur Verwaltung der App-Einstellungen. Kapselt den Zugriff auf SharedPreferences und
 * bietet StateFlows für alle Settings.
 *
 * Features:
 * - Zentrale Verwaltung aller App-Einstellungen
 * - Reactive StateFlows für UI-Updates
 * - Persistierung in SharedPreferences
 * - Crashlytics-Integration für Error-Tracking
 * - Analytics-Tracking für Einstellungsänderungen
 *
 * @constructor Initialisiert das Repository mit dem Anwendungskontext
 * @param context Anwendungskontext für SharedPreferences
 */
class SettingsRepository(context: Context) {
    /** SharedPreferences-Instanz für die Speicherung der Einstellungen */
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // --- StateFlows für alle Einstellungen ---

    /** StateFlow für Benachrichtigungen */
    private val _notificationsEnabled =
        MutableStateFlow(
            sharedPreferences.getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true)
        )

    /** Gibt an, ob Benachrichtigungen aktiviert sind */
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    /** StateFlow für Auto-Refresh */
    private val _autoRefreshEnabled =
        MutableStateFlow(
            sharedPreferences.getBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, true)
        )

    /** Gibt an, ob Auto-Refresh aktiviert ist */
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    /** StateFlow für Bildqualität */
    private val _imageQuality =
        MutableStateFlow(
            ImageQuality.valueOf(
                sharedPreferences.getString(
                    Constants.PREF_IMAGE_QUALITY,
                    ImageQuality.HIGH.name
                )
                    ?: ImageQuality.HIGH.name
            )
        )

    /** Aktuelle Bildqualität */
    val imageQuality: StateFlow<ImageQuality> = _imageQuality.asStateFlow()

    /** StateFlow für Spracheinstellung */
    private val _language =
        MutableStateFlow(
            sharedPreferences.getString(Constants.PREF_LANGUAGE, "system") ?: "system"
        )

    /** Aktuelle Spracheinstellung */
    val language: StateFlow<String> = _language.asStateFlow()

    /** StateFlow für Gaming-Modus */
    private val _gamingModeEnabled =
        MutableStateFlow(
            sharedPreferences.getBoolean(Constants.PREF_GAMING_MODE_ENABLED, false)
        )

    /** Gibt an, ob der Gaming-Modus aktiviert ist */
    val gamingModeEnabled: StateFlow<Boolean> = _gamingModeEnabled.asStateFlow()

    /** StateFlow für Performance-Modus */
    private val _performanceModeEnabled =
        MutableStateFlow(
            sharedPreferences.getBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, false)
        )

    /** Gibt an, ob der Performance-Modus aktiviert ist */
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()

    /** StateFlow für Share Games */
    private val _shareGamesEnabled =
        MutableStateFlow(sharedPreferences.getBoolean(Constants.PREF_SHARE_GAMES_ENABLED, true))

    /** Gibt an, ob das Teilen von Spielen aktiviert ist */
    val shareGamesEnabled: StateFlow<Boolean> = _shareGamesEnabled.asStateFlow()

    /** StateFlow für Dark Mode */
    private val _darkModeEnabled =
        MutableStateFlow(sharedPreferences.getBoolean(Constants.PREF_DARK_MODE_ENABLED, false))

    /** Gibt an, ob der Dark Mode aktiviert ist */
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    /** StateFlow für Werbung */
    private val _adsEnabled =
        MutableStateFlow(sharedPreferences.getBoolean(Constants.PREF_ADS_ENABLED, true))

    /** Gibt an, ob Werbung aktiviert ist */
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    /** StateFlow für Analytics (immer aktiviert) */
    private val _analyticsEnabled = MutableStateFlow(true)

    /** Gibt an, ob Analytics aktiviert ist (immer true) */
    val analyticsEnabled: StateFlow<Boolean> = _analyticsEnabled.asStateFlow()

    // --- Methoden zum Setzen der Einstellungen ---

    /**
     * Setzt, ob Benachrichtigungen aktiviert sind.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, enabled) }
            _notificationsEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("notifications_enabled", enabled)
            AppLogger.d("SettingsRepository", "Notifications enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "notifications_enabled",
                _notificationsEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von notifications_enabled", e)
        }
    }

    /**
     * Setzt, ob Auto-Refresh aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setAutoRefreshEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, enabled) }
            _autoRefreshEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("auto_refresh_enabled", enabled)
            AppLogger.d("SettingsRepository", "Auto refresh enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "auto_refresh_enabled",
                _autoRefreshEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von auto_refresh_enabled", e)
        }
    }

    /**
     * Setzt die Bildqualität.
     * @param quality Die gewünschte Bildqualität
     */
    fun setImageQuality(quality: ImageQuality) {
        try {
            sharedPreferences.edit { putString(Constants.PREF_IMAGE_QUALITY, quality.name) }
            _imageQuality.value = quality
            CrashlyticsHelper.setCustomKey("image_quality", quality.name)
            AppLogger.d("SettingsRepository", "Image quality set: ${quality.name}")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "image_quality",
                _imageQuality.value.name,
                quality.name,
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von image_quality", e)
        }
    }

    /**
     * Setzt die Spracheinstellung.
     * @param language Die Sprachkennung (z.B. "de", "en", "system")
     */
    fun setLanguage(language: String) {
        try {
            sharedPreferences.edit { putString(Constants.PREF_LANGUAGE, language) }
            _language.value = language
            CrashlyticsHelper.setCustomKey("app_language", language)
            AppLogger.d("SettingsRepository", "Language set: $language")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "language",
                _language.value,
                language,
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von language", e)
        }
    }

    /**
     * Setzt, ob der Gaming-Modus aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setGamingModeEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_GAMING_MODE_ENABLED, enabled) }
            _gamingModeEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("gaming_mode_enabled", enabled)
            AppLogger.d("SettingsRepository", "Gaming mode enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "gaming_mode_enabled",
                _gamingModeEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von gaming_mode_enabled", e)
        }
    }

    /**
     * Setzt, ob der Performance-Modus aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setPerformanceModeEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, enabled) }
            _performanceModeEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("performance_mode_enabled", enabled)
            AppLogger.d("SettingsRepository", "Performance mode enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "performance_mode_enabled",
                _performanceModeEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von performance_mode_enabled", e)
        }
    }

    /**
     * Setzt, ob das Teilen von Spielen aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setShareGamesEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_SHARE_GAMES_ENABLED, enabled) }
            _shareGamesEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("share_games_enabled", enabled)
            AppLogger.d("SettingsRepository", "Share games enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "share_games_enabled",
                _shareGamesEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von share_games_enabled", e)
        }
    }

    /**
     * Setzt, ob der Dark Mode aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_DARK_MODE_ENABLED, enabled) }
            _darkModeEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("dark_mode_enabled", enabled)
            AppLogger.d("SettingsRepository", "Dark mode enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "dark_mode_enabled",
                _darkModeEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von dark_mode_enabled", e)
        }
    }

    /**
     * Setzt, ob Werbung aktiviert ist.
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setAdsEnabled(enabled: Boolean) {
        try {
            sharedPreferences.edit { putBoolean(Constants.PREF_ADS_ENABLED, enabled) }
            _adsEnabled.value = enabled
            CrashlyticsHelper.setCustomKey("ads_enabled", enabled)
            AppLogger.d("SettingsRepository", "Ads enabled: $enabled")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "ads_enabled",
                _adsEnabled.value.toString(),
                enabled.toString(),
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Setzen von ads_enabled", e)
        }
    }

    /** Setzt, ob Analytics aktiviert ist (immer true). */
    fun setAnalyticsEnabled() {
        // Analytics ist immer aktiviert
        _analyticsEnabled.value = true
        CrashlyticsHelper.setCustomKey("analytics_enabled", true)
        AppLogger.d("SettingsRepository", "Analytics always enabled: true")
    }

    /**
     * Gibt alle aktuellen Einstellungen als Map zurück.
     * @return Map mit allen Einstellungen
     */
    fun getAllSettings(): Map<String, Any> {
        return mapOf(
            "notifications_enabled" to _notificationsEnabled.value,
            "auto_refresh_enabled" to _autoRefreshEnabled.value,
            "image_quality" to _imageQuality.value.name,
            "language" to _language.value,
            "gaming_mode_enabled" to _gamingModeEnabled.value,
            "performance_mode_enabled" to _performanceModeEnabled.value,
            "share_games_enabled" to _shareGamesEnabled.value,
            "dark_mode_enabled" to _darkModeEnabled.value,
            "ads_enabled" to _adsEnabled.value,
            "analytics_enabled" to _analyticsEnabled.value
        )
    }

    /** Setzt alle Einstellungen auf Standardwerte zurück. */
    fun resetToDefaults() {
        try {
            setNotificationsEnabled(true)
            setAutoRefreshEnabled(true)
            setImageQuality(ImageQuality.HIGH)
            setLanguage("system")
            setGamingModeEnabled(true)
            setPerformanceModeEnabled(true)
            setShareGamesEnabled(true)
            setDarkModeEnabled(false)
            setAdsEnabled(true)
            setAnalyticsEnabled()

            CrashlyticsHelper.setCustomKey("settings_reset_to_defaults", true)
            AppLogger.d("SettingsRepository", "Alle Einstellungen auf Standardwerte zurückgesetzt")
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "reset_to_defaults",
                "current_settings",
                "default_settings",
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsRepository", "Fehler beim Zurücksetzen der Einstellungen", e)
        }
    }
}
