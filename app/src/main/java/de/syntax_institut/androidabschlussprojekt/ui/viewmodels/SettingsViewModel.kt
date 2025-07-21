package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel für die Einstellungen mit vollständiger SettingsUiState-Integration.
 *
 * Features:
 * - Zentrale Verwaltung aller App-Einstellungen
 * - Reactive UI-State mit StateFlow
 * - Crashlytics-Integration für Error-Tracking
 * - Analytics-Tracking für Einstellungsänderungen
 * - Cache-Statistik-Management
 * - Robuste Fehlerbehandlung
 * - Pro-Status und Feature-Freischaltungen
 *
 * @param settingsRepository Repository für Einstellungsdaten
 */
class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Initialisiere UI State mit Repository-Werten
        viewModelScope.launch {
            combine(
                listOf<Flow<Any>>(
                    settingsRepository.notificationsEnabled,
                    settingsRepository.autoRefreshEnabled,
                    settingsRepository.imageQuality,
                    settingsRepository.language,
                    settingsRepository.gamingModeEnabled,
                    settingsRepository.performanceModeEnabled,
                    settingsRepository.shareGamesEnabled,
                    settingsRepository.darkModeEnabled,
                    settingsRepository.adsEnabled,
                    settingsRepository.analyticsEnabled
                )
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                SettingsUiState(
                    notificationsEnabled = values[0] as Boolean,
                    autoRefreshEnabled = values[1] as Boolean,
                    imageQuality = values[2] as ImageQuality,
                    language = values[3] as String,
                    gamingModeEnabled = values[4] as Boolean,
                    performanceModeEnabled = values[5] as Boolean,
                    shareGamesEnabled = values[6] as Boolean,
                    darkModeEnabled = values[7] as Boolean,
                    adsEnabled = values[8] as Boolean,
                    analyticsEnabled = values[9] as Boolean
                )
            }
                .collect { updatedState -> _uiState.value = updatedState }
        }

        // Setze Custom Keys für Crashlytics
        CrashlyticsHelper.setCustomKey("settings_viewmodel_initialized", true)
        AppLogger.d("SettingsViewModel", "SettingsViewModel initialisiert")
    }

    /**
     * Setzt, ob Werbung (AdMob) angezeigt werden darf (Opt-In). Aktualisiert sowohl Repository als
     * auch UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setAdsEnabled(enabled: Boolean) {
        settingsRepository.setAdsEnabled(enabled)
        _uiState.value = _uiState.value.copy(adsEnabled = enabled)
        CrashlyticsHelper.setCustomKey("ads_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Ads enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("ads_setting_changed")
        AppAnalytics.trackPerformanceMetric("ads_enabled", if (enabled) 1 else 0, "boolean")
    }

    /**
     * Setzt, ob Benachrichtigungen aktiviert sind. Aktualisiert sowohl Repository als auch
     * UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        CrashlyticsHelper.setCustomKey("notifications_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Notifications enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("notifications_setting_changed")
    }

    /**
     * Setzt, ob automatische Aktualisierung aktiviert ist. Aktualisiert sowohl Repository als auch
     * UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setAutoRefreshEnabled(enabled: Boolean) {
        settingsRepository.setAutoRefreshEnabled(enabled)
        _uiState.value = _uiState.value.copy(autoRefreshEnabled = enabled)
        CrashlyticsHelper.setCustomKey("auto_refresh_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Auto refresh enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("auto_refresh_setting_changed")
    }

    /**
     * Setzt die Bildqualität für Spielbilder. Aktualisiert sowohl Repository als auch UI-State.
     *
     * @param quality Die gewünschte Bildqualität (LOW, MEDIUM, HIGH)
     */
    fun setImageQuality(quality: ImageQuality) {
        settingsRepository.setImageQuality(quality)
        _uiState.value = _uiState.value.copy(imageQuality = quality)
        CrashlyticsHelper.setCustomKey("image_quality", quality.name)
        AppLogger.d("SettingsViewModel", "Image quality set: ${quality.name}")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("image_quality_setting_changed")
        AppAnalytics.trackPerformanceMetric("image_quality", quality.ordinal, "enum")
    }

    /**
     * Setzt die Spracheinstellung. Aktualisiert sowohl Repository als auch UI-State.
     *
     * @param lang Die Sprachkennung (z.B. "de", "en")
     */
    fun setLanguage(lang: String) {
        // Prüfe, ob die Sprache unterstützt wird
        if (LocaleManager.isLanguageSupported(lang)) {
            settingsRepository.setLanguage(lang)
            _uiState.value = _uiState.value.copy(language = lang)
            CrashlyticsHelper.setCustomKey("app_language", lang)
            AppLogger.d("SettingsViewModel", "Language set: $lang")

            // Analytics-Tracking
            AppAnalytics.trackUserAction("language_setting_changed")
            AppAnalytics.trackPerformanceMetric("language_code", lang.length, "characters")

            // Logge die Sprachänderung für Debugging
            AppLogger.i("SettingsViewModel", "Sprache erfolgreich geändert zu: $lang")
        } else {
            AppLogger.w("SettingsViewModel", "Unsupported language: $lang")
            CrashlyticsHelper.setCustomKey("unsupported_language", lang)

            // Error-Tracking
            AppAnalytics.trackError("Unsupported language: $lang", "SettingsViewModel")
        }
    }

    /** Gibt die verfügbaren Sprachen für die UI zurück. */
    fun getAvailableLanguages(): Map<String, String> {
        val languages = LocaleManager.getAvailableLanguagesForUI()

        // Analytics-Tracking für Sprachabfrage
        AppAnalytics.trackUserAction("available_languages_requested")
        AppAnalytics.trackPerformanceMetric("available_languages_count", languages.size, "count")

        return languages
    }

    /**
     * Setzt, ob der Gaming-Modus aktiviert ist. Aktualisiert sowohl Repository als auch UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setGamingModeEnabled(enabled: Boolean) {
        settingsRepository.setGamingModeEnabled(enabled)
        _uiState.value = _uiState.value.copy(gamingModeEnabled = enabled)
        CrashlyticsHelper.setCustomKey("gaming_mode_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Gaming mode enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("gaming_mode_setting_changed")
    }

    /**
     * Setzt, ob der Performance-Modus aktiviert ist. Aktualisiert sowohl Repository als auch
     * UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setPerformanceModeEnabled(enabled: Boolean) {
        settingsRepository.setPerformanceModeEnabled(enabled)
        _uiState.value = _uiState.value.copy(performanceModeEnabled = enabled)
        CrashlyticsHelper.setCustomKey("performance_mode_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Performance mode enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("performance_mode_setting_changed")
    }

    /**
     * Setzt, ob das Teilen von Spielen aktiviert ist. Aktualisiert sowohl Repository als auch
     * UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setShareGamesEnabled(enabled: Boolean) {
        settingsRepository.setShareGamesEnabled(enabled)
        _uiState.value = _uiState.value.copy(shareGamesEnabled = enabled)
        CrashlyticsHelper.setCustomKey("share_games_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Share games enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("share_games_setting_changed")
    }

    /**
     * Setzt, ob der Dark Mode aktiviert ist. Aktualisiert sowohl Repository als auch UI-State.
     *
     * @param enabled true für aktiviert, false für deaktiviert
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        settingsRepository.setDarkModeEnabled(enabled)
        _uiState.value = _uiState.value.copy(darkModeEnabled = enabled)
        CrashlyticsHelper.setCustomKey("dark_mode_enabled", enabled)
        AppLogger.d("SettingsViewModel", "Dark mode enabled: $enabled")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("dark_mode_setting_changed")
    }

    /**
     * Setzt Cache-Statistiken im UI-State. Wird von der UI aufgerufen, um Cache-Informationen
     * anzuzeigen.
     *
     * @param cacheSize Aktuelle Cache-Größe in Bytes
     * @param maxCacheSize Maximale Cache-Größe in Bytes
     * @param lastSyncTime Zeitstempel der letzten Synchronisation
     */
    fun updateCacheStats(cacheSize: Int, maxCacheSize: Int, lastSyncTime: Long?) {
        _uiState.value =
            _uiState.value.copy(
                cacheSize = cacheSize,
                maxCacheSize = maxCacheSize,
                lastSyncTime = lastSyncTime
            )
        CrashlyticsHelper.setCustomKey("cache_size", cacheSize)
        CrashlyticsHelper.setCustomKey("max_cache_size", maxCacheSize)
        AppLogger.d("SettingsViewModel", "Cache stats updated: $cacheSize/$maxCacheSize")

        // Analytics-Tracking
        AppAnalytics.trackPerformanceMetric("cache_size", cacheSize, "bytes")
        AppAnalytics.trackPerformanceMetric(
            "cache_usage_percentage",
            if (maxCacheSize > 0) (cacheSize * 100 / maxCacheSize) else 0,
            "percentage"
        )
    }

    /**
     * Setzt Loading-State für UI-Updates.
     *
     * @param isLoading true wenn geladen wird, false sonst
     */
    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
        AppLogger.d("SettingsViewModel", "Loading state: $isLoading")

        // Analytics-Tracking
        AppAnalytics.trackUserAction("settings_loading_state_changed")
        AppAnalytics.trackPerformanceMetric("settings_loading", if (isLoading) 1 else 0, "boolean")
    }

    /**
     * Setzt Error-State für Fehlerbehandlung in der UI. Zeichnet Fehler in Crashlytics auf.
     *
     * @param error Die Fehlermeldung oder null für keine Fehler
     */
    fun setError(error: String?) {
        _uiState.value = _uiState.value.copy(error = error)
        if (error != null) {
            CrashlyticsHelper.setCustomKey("settings_error", error)
            AppLogger.e("SettingsViewModel", "Error: $error")

            // Crashlytics Error Recording
            CrashlyticsHelper.recordSettingsError(
                "general_settings",
                "no_error",
                "error_occurred",
                error
            )

            // Analytics-Tracking
            AppAnalytics.trackError("Settings error: $error", "SettingsViewModel")
        }
    }

    /**
     * Setzt alle Einstellungen auf Standardwerte zurück. Ruft das Repository auf und aktualisiert
     * den UI-State.
     */
    fun resetToDefaults() {
        try {
            setLoading(true)
            settingsRepository.resetToDefaults()
            CrashlyticsHelper.setCustomKey("settings_reset_to_defaults", true)
            AppLogger.d("SettingsViewModel", "Alle Einstellungen auf Standardwerte zurückgesetzt")

            // Analytics-Tracking
            AppAnalytics.trackUserAction("settings_reset_to_defaults")

            setLoading(false)
        } catch (e: Exception) {
            CrashlyticsHelper.recordSettingsError(
                "reset_to_defaults",
                "current_settings",
                "default_settings",
                e.message ?: "Unknown error"
            )
            AppLogger.e("SettingsViewModel", "Fehler beim Zurücksetzen der Einstellungen", e)
            setError("Fehler beim Zurücksetzen der Einstellungen: ${e.message}")
            setLoading(false)
        }
    }

    /**
     * Gibt alle aktuellen Einstellungen als Map zurück. Wird für Debugging und Analytics verwendet.
     *
     * @return Map mit allen aktuellen Einstellungen
     */
    fun getAllSettings(): Map<String, Any> {
        val settings = settingsRepository.getAllSettings()

        // Analytics-Tracking
        AppAnalytics.trackUserAction("all_settings_requested")
        AppAnalytics.trackPerformanceMetric("settings_count", settings.size, "count")

        return settings
    }
}
