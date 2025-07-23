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
    private val sharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // StateFlows für alle Einstellungen mit Standardwerten aus SharedPreferences
    private val _notificationsEnabled = MutableStateFlow(true)
    private val _autoRefreshEnabled = MutableStateFlow(true)
    private val _imageQuality = MutableStateFlow(ImageQuality.HIGH)
    private val _language = MutableStateFlow("system")
    private val _gamingModeEnabled = MutableStateFlow(false)
    private val _performanceModeEnabled = MutableStateFlow(true)
    private val _shareGamesEnabled = MutableStateFlow(true)
    private val _darkModeEnabled = MutableStateFlow(false)
    private val _analyticsEnabled = MutableStateFlow(false)
    private val _adsEnabled = MutableStateFlow(true)

    // Public StateFlows
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()
    val imageQuality: StateFlow<ImageQuality> = _imageQuality.asStateFlow()
    val language: StateFlow<String> = _language.asStateFlow()
    val gamingModeEnabled: StateFlow<Boolean> = _gamingModeEnabled.asStateFlow()
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()
    val shareGamesEnabled: StateFlow<Boolean> = _shareGamesEnabled.asStateFlow()
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()
    val analyticsEnabled: StateFlow<Boolean> = _analyticsEnabled.asStateFlow()
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        try {
            _notificationsEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true)
            _autoRefreshEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, true)
            _imageQuality.value = ImageQuality.valueOf(
                sharedPreferences.getString(Constants.PREF_IMAGE_QUALITY, ImageQuality.HIGH.name)
                    ?: ImageQuality.HIGH.name
            )
            _language.value =
                sharedPreferences.getString(Constants.PREF_LANGUAGE, "system") ?: "system"
            _gamingModeEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_GAMING_MODE_ENABLED, false)
            _performanceModeEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, true)
            _shareGamesEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_SHARE_GAMES_ENABLED, true)
            _darkModeEnabled.value =
                sharedPreferences.getBoolean(Constants.PREF_DARK_MODE_ENABLED, false)
            _analyticsEnabled.value = sharedPreferences.getBoolean("analytics_enabled", false)
            _adsEnabled.value = sharedPreferences.getBoolean("ads_enabled", true)
        } catch (e: Exception) {
            CrashlyticsHelper.recordException(e)
            AppLogger.e("SettingsRepository", "Error loading settings", e)
        }
    }

    private fun saveSettings() {
        sharedPreferences.edit {
            putBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, _notificationsEnabled.value)
            putBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, _autoRefreshEnabled.value)
            putString(Constants.PREF_IMAGE_QUALITY, _imageQuality.value.name)
            putString(Constants.PREF_LANGUAGE, _language.value)
            putBoolean(Constants.PREF_GAMING_MODE_ENABLED, _gamingModeEnabled.value)
            putBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, _performanceModeEnabled.value)
            putBoolean(Constants.PREF_SHARE_GAMES_ENABLED, _shareGamesEnabled.value)
            putBoolean(Constants.PREF_DARK_MODE_ENABLED, _darkModeEnabled.value)
            putBoolean("analytics_enabled", _analyticsEnabled.value)
            putBoolean("ads_enabled", _adsEnabled.value)
        }
    }

    // Setter methods with error handling and logging
    fun setNotificationsEnabled(enabled: Boolean) = updateSetting(
        { _notificationsEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, it) } },
        enabled,
        "notifications_enabled"
    )

    fun setAutoRefreshEnabled(enabled: Boolean) = updateSetting(
        { _autoRefreshEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, it) } },
        enabled,
        "auto_refresh_enabled"
    )

    fun setImageQuality(quality: ImageQuality) {
        try {
            _imageQuality.value = quality
            sharedPreferences.edit { putString(Constants.PREF_IMAGE_QUALITY, quality.name) }
            AppLogger.d("SettingsRepository", "Image quality set to: ${quality.name}")
            CrashlyticsHelper.setCustomKey("image_quality", quality.name)
        } catch (e: Exception) {
            handleError("image_quality", e)
        }
    }

    fun setLanguage(lang: String) = updateSetting(
        { _language.value = it },
        { sharedPreferences.edit { putString(Constants.PREF_LANGUAGE, it) } },
        lang,
        "language"
    )

    fun setGamingModeEnabled(enabled: Boolean) = updateSetting(
        { _gamingModeEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_GAMING_MODE_ENABLED, it) } },
        enabled,
        "gaming_mode_enabled"
    )

    fun setPerformanceModeEnabled(enabled: Boolean) = updateSetting(
        { _performanceModeEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, it) } },
        enabled,
        "performance_mode_enabled"
    )

    fun setShareGamesEnabled(enabled: Boolean) = updateSetting(
        { _shareGamesEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_SHARE_GAMES_ENABLED, it) } },
        enabled,
        "share_games_enabled"
    )

    fun setDarkModeEnabled(enabled: Boolean) = updateSetting(
        { _darkModeEnabled.value = it },
        { sharedPreferences.edit { putBoolean(Constants.PREF_DARK_MODE_ENABLED, it) } },
        enabled,
        "dark_mode_enabled"
    )

    fun setAnalyticsEnabled(enabled: Boolean) = updateSetting(
        { _analyticsEnabled.value = it },
        { sharedPreferences.edit { putBoolean("analytics_enabled", it) } },
        enabled,
        "analytics_enabled"
    )

    fun setAdsEnabled(enabled: Boolean) = updateSetting(
        { _adsEnabled.value = it },
        { sharedPreferences.edit { putBoolean("ads_enabled", it) } },
        enabled,
        "ads_enabled"
    )

    // Helper functions
    private inline fun <T> updateSetting(
        updateFlow: (T) -> Unit,
        updatePrefs: (T) -> Unit,
        value: T,
        settingName: String,
    ) {
        try {
            updateFlow(value)
            updatePrefs(value)
            AppLogger.d("SettingsRepository", "Setting $settingName updated to: $value")
            CrashlyticsHelper.setCustomKey(settingName, value.toString())
        } catch (e: Exception) {
            handleError(settingName, e)
        }
    }

    private fun handleError(settingName: String, e: Exception) {
        val errorMsg = "Error updating setting: $settingName - ${e.message}"
        AppLogger.e("SettingsRepository", errorMsg, e)
        CrashlyticsHelper.recordException(e)
    }

    fun getAllSettings(): Map<String, Any> = mapOf(
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

    fun resetToDefaults() {
        try {
            setNotificationsEnabled(true)
            setAutoRefreshEnabled(true)
            setImageQuality(ImageQuality.HIGH)
            setLanguage("system")
            setGamingModeEnabled(false)
            setPerformanceModeEnabled(true)
            setShareGamesEnabled(true)
            setDarkModeEnabled(false)
            setAnalyticsEnabled(false)
            setAdsEnabled(true)
            AppLogger.d("SettingsRepository", "All settings reset to defaults")
        } catch (e: Exception) {
            handleError("reset_to_defaults", e)
        }
    }
}