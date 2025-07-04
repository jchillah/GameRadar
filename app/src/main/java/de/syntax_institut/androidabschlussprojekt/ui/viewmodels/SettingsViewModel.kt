package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import android.content.*
import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.*

class SettingsViewModel : ViewModel(), KoinComponent {

    private val gameRepository: GameRepository by inject()
    private lateinit var sharedPreferences: SharedPreferences

    // Benachrichtigungen
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Auto-Refresh
    private val _autoRefreshEnabled = MutableStateFlow(true)
    val autoRefreshEnabled: StateFlow<Boolean> = _autoRefreshEnabled.asStateFlow()

    // Bildqualität
    private val _imageQuality = MutableStateFlow(ImageQuality.HIGH)
    val imageQuality: StateFlow<ImageQuality> = _imageQuality.asStateFlow()

    // Sprache
    private val _language = MutableStateFlow("Deutsch")
    val language: StateFlow<String> = _language.asStateFlow()

    // Gaming-Features
    private val _gamingModeEnabled = MutableStateFlow(false)
    val gamingModeEnabled: StateFlow<Boolean> = _gamingModeEnabled.asStateFlow()

    private val _performanceModeEnabled = MutableStateFlow(true)
    val performanceModeEnabled: StateFlow<Boolean> = _performanceModeEnabled.asStateFlow()

    private val _shareGamesEnabled = MutableStateFlow(true)
    val shareGamesEnabled: StateFlow<Boolean> = _shareGamesEnabled.asStateFlow()

    // Cache-Status
    private val _cacheSize = MutableStateFlow(0)
    val cacheSize: StateFlow<Int> = _cacheSize.asStateFlow()

    // Dialog-States
    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    private val _showPrivacyDialog = MutableStateFlow(false)
    val showPrivacyDialog: StateFlow<Boolean> = _showPrivacyDialog.asStateFlow()

    fun initialize(context: Context) {
        sharedPreferences =
            context.getSharedPreferences("gamefinder_settings", Context.MODE_PRIVATE)
        loadSettings()
        updateCacheSize()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "notifications", "enabled" to enabled)
        )
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        _autoRefreshEnabled.value = enabled
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "auto_refresh", "enabled" to enabled)
        )
    }

    fun setImageQuality(quality: ImageQuality) {
        _imageQuality.value = quality
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "image_quality", "quality" to quality.displayName)
        )
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        saveSettings()
        Analytics.trackEvent("settings_changed", mapOf("setting" to "language", "language" to lang))
    }

    fun setGamingModeEnabled(enabled: Boolean) {
        _gamingModeEnabled.value = enabled
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "gaming_mode", "enabled" to enabled)
        )
    }

    fun setPerformanceModeEnabled(enabled: Boolean) {
        _performanceModeEnabled.value = enabled
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "performance_mode", "enabled" to enabled)
        )
    }

    fun setShareGamesEnabled(enabled: Boolean) {
        _shareGamesEnabled.value = enabled
        saveSettings()
        Analytics.trackEvent(
            "settings_changed",
            mapOf("setting" to "share_games", "enabled" to enabled)
        )
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                gameRepository.clearCache()
                _cacheSize.value = 0
                Analytics.trackEvent(
                    "cache_cleared",
                    mapOf("cache_size_before" to _cacheSize.value)
                )
            } catch (e: Exception) {
                Analytics.trackError("cache_clear_failed", "SettingsViewModel")
            }
        }
    }

    fun optimizeCache() {
        viewModelScope.launch {
            try {
                // Cache-Optimierung: Entferne alte Einträge
                val currentSize = gameRepository.getCacheSize()
                gameRepository.optimizeCache()
                _cacheSize.value = gameRepository.getCacheSize()
                Analytics.trackEvent(
                    "cache_optimized",
                    mapOf(
                        "cache_size_before" to currentSize,
                        "cache_size_after" to _cacheSize.value
                    )
                )
            } catch (e: Exception) {
                Analytics.trackError("cache_optimization_failed", "SettingsViewModel")
            }
        }
    }

    fun aboutApp() {
        Analytics.trackEvent("about_app_opened")
        _showAboutDialog.value = true
    }

    fun privacyPolicy() {
        Analytics.trackEvent("privacy_policy_opened")
        _showPrivacyDialog.value = true
    }

    fun dismissAboutDialog() {
        _showAboutDialog.value = false
    }

    fun dismissPrivacyDialog() {
        _showPrivacyDialog.value = false
    }

    private fun saveSettings() {
        viewModelScope.launch {
            try {
                sharedPreferences.edit().apply {
                    putBoolean("notifications_enabled", _notificationsEnabled.value)
                    putBoolean("auto_refresh_enabled", _autoRefreshEnabled.value)
                    putString("image_quality", _imageQuality.value.name)
                    putString("language", _language.value)
                    putBoolean("gaming_mode_enabled", _gamingModeEnabled.value)
                    putBoolean("performance_mode_enabled", _performanceModeEnabled.value)
                    putBoolean("share_games_enabled", _shareGamesEnabled.value)
                }.apply()
            } catch (e: Exception) {
                Analytics.trackError("settings_save_failed", "SettingsViewModel")
            }
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            try {
                if (::sharedPreferences.isInitialized) {
                    _notificationsEnabled.value =
                        sharedPreferences.getBoolean("notifications_enabled", true)
                    _autoRefreshEnabled.value =
                        sharedPreferences.getBoolean("auto_refresh_enabled", true)
                    _imageQuality.value = ImageQuality.valueOf(
                        sharedPreferences.getString(
                            "image_quality",
                            ImageQuality.HIGH.name
                        ) ?: ImageQuality.HIGH.name
                    )
                    _language.value =
                        sharedPreferences.getString("language", "Deutsch") ?: "Deutsch"
                    _gamingModeEnabled.value =
                        sharedPreferences.getBoolean("gaming_mode_enabled", false)
                    _performanceModeEnabled.value =
                        sharedPreferences.getBoolean("performance_mode_enabled", true)
                    _shareGamesEnabled.value =
                        sharedPreferences.getBoolean("share_games_enabled", true)
                }
            } catch (e: Exception) {
                Analytics.trackError("settings_load_failed", "SettingsViewModel")
            }
        }
    }

    private fun updateCacheSize() {
        viewModelScope.launch {
            try {
                _cacheSize.value = gameRepository.getCacheSize()
            } catch (e: Exception) {
                Analytics.trackError("cache_size_update_failed", "SettingsViewModel")
            }
        }
    }
} 