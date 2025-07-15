package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import kotlinx.coroutines.flow.*

class SettingsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _autoRefreshEnabled = MutableStateFlow(true)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    private val _imageQuality = MutableStateFlow(ImageQuality.HIGH)
    val imageQuality: StateFlow<ImageQuality> = _imageQuality.asStateFlow()

    private val _language = MutableStateFlow("system")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _gamingModeEnabled = MutableStateFlow(false)
    val gamingModeEnabled: StateFlow<Boolean> = _gamingModeEnabled.asStateFlow()

    private val _performanceModeEnabled = MutableStateFlow(true)
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()

    private val _shareGamesEnabled = MutableStateFlow(true)
    val shareGamesEnabled: StateFlow<Boolean> = _shareGamesEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _analyticsEnabled = MutableStateFlow(false)
    val analyticsEnabled: StateFlow<Boolean> = _analyticsEnabled.asStateFlow()

    private val _adsEnabled = MutableStateFlow(false)
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    init {
        loadSettings()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        saveSettings()
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        _autoRefreshEnabled.value = enabled
        saveSettings()
    }

    fun setImageQuality(quality: ImageQuality) {
        _imageQuality.value = quality
        saveSettings()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        saveSettings()
    }

    fun setGamingModeEnabled(enabled: Boolean) {
        _gamingModeEnabled.value = enabled
        saveSettings()
    }

    fun setPerformanceModeEnabled(enabled: Boolean) {
        _performanceModeEnabled.value = enabled
        saveSettings()
    }

    fun setShareGamesEnabled(enabled: Boolean) {
        _shareGamesEnabled.value = enabled
        saveSettings()
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        saveSettings()
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        _analyticsEnabled.value = enabled
        saveSettings()
    }

    fun setAdsEnabled(enabled: Boolean) {
        _adsEnabled.value = enabled
        saveSettings()
    }

    private fun saveSettings() {
        sharedPreferences.edit().apply {
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
            apply()
        }
    }

    private fun loadSettings() {
        _notificationsEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_NOTIFICATIONS_ENABLED, true)
        _autoRefreshEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_AUTO_REFRESH_ENABLED, true)
        _imageQuality.value =
            ImageQuality.valueOf(
                sharedPreferences.getString(
                    Constants.PREF_IMAGE_QUALITY,
                    ImageQuality.HIGH.name
                )
                    ?: ImageQuality.HIGH.name
            )
        _language.value = sharedPreferences.getString(Constants.PREF_LANGUAGE, "system") ?: "system"
        _gamingModeEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_GAMING_MODE_ENABLED, false)
        _performanceModeEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_PERFORMANCE_MODE_ENABLED, true)
        _shareGamesEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_SHARE_GAMES_ENABLED, true)
        _darkModeEnabled.value =
            sharedPreferences.getBoolean(Constants.PREF_DARK_MODE_ENABLED, false)
        _analyticsEnabled.value = sharedPreferences.getBoolean("analytics_enabled", false)
        _adsEnabled.value = sharedPreferences.getBoolean("ads_enabled", false)
    }
}
