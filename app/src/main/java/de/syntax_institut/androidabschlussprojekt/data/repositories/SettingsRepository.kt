package de.syntax_institut.androidabschlussprojekt.data.repositories

import android.content.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import kotlinx.coroutines.flow.*

class SettingsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("gameradar_settings", Context.MODE_PRIVATE)

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _autoRefreshEnabled = MutableStateFlow(true)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    private val _imageQuality = MutableStateFlow(ImageQuality.HIGH)
    val imageQuality: StateFlow<ImageQuality> = _imageQuality.asStateFlow()

    private val _language = MutableStateFlow("Deutsch")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _gamingModeEnabled = MutableStateFlow(false)
    val gamingModeEnabled: StateFlow<Boolean> = _gamingModeEnabled.asStateFlow()

    private val _performanceModeEnabled = MutableStateFlow(true)
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()

    private val _shareGamesEnabled = MutableStateFlow(true)
    val shareGamesEnabled: StateFlow<Boolean> = _shareGamesEnabled.asStateFlow()

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

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

    private fun saveSettings() {
        sharedPreferences.edit().apply {
            putBoolean("notifications_enabled", _notificationsEnabled.value)
            putBoolean("auto_refresh_enabled", _autoRefreshEnabled.value)
            putString("image_quality", _imageQuality.value.name)
            putString("language", _language.value)
            putBoolean("gaming_mode_enabled", _gamingModeEnabled.value)
            putBoolean("performance_mode_enabled", _performanceModeEnabled.value)
            putBoolean("share_games_enabled", _shareGamesEnabled.value)
            putBoolean("dark_mode_enabled", _darkModeEnabled.value)
            apply()
        }
    }

    private fun loadSettings() {
        _notificationsEnabled.value = sharedPreferences.getBoolean("notifications_enabled", true)
        _autoRefreshEnabled.value = sharedPreferences.getBoolean("auto_refresh_enabled", true)
        _imageQuality.value = ImageQuality.valueOf(
            sharedPreferences.getString("image_quality", ImageQuality.HIGH.name)
                ?: ImageQuality.HIGH.name
        )
        _language.value = sharedPreferences.getString("language", "Deutsch") ?: "Deutsch"
        _gamingModeEnabled.value = sharedPreferences.getBoolean("gaming_mode_enabled", false)
        _performanceModeEnabled.value =
            sharedPreferences.getBoolean("performance_mode_enabled", true)
        _shareGamesEnabled.value = sharedPreferences.getBoolean("share_games_enabled", true)
        _darkModeEnabled.value = sharedPreferences.getBoolean("dark_mode_enabled", false)
    }
} 