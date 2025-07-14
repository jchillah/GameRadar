package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val analyticsEnabled =
        settingsRepository.analyticsEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val notificationsEnabled =
        settingsRepository.notificationsEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val autoRefreshEnabled =
        settingsRepository.autoRefreshEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            true
        )

    val imageQuality =
        settingsRepository.imageQuality.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ImageQuality.HIGH
        )

    val language =
        settingsRepository.language.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            "system"
        )

    val gamingModeEnabled =
        settingsRepository.gamingModeEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val performanceModeEnabled =
        settingsRepository.performanceModeEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val shareGamesEnabled =
        settingsRepository.shareGamesEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            true
        )

    val darkModeEnabled =
        settingsRepository.darkModeEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAnalyticsEnabled(enabled)
            // Crashlytics basierend auf Analytics-Einstellung steuern
            CrashlyticsHelper.setCrashlyticsEnabled(enabled)
            // Custom Key für besseres Tracking
            CrashlyticsHelper.setCustomKey("analytics_enabled", enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
            CrashlyticsHelper.setCustomKey("notifications_enabled", enabled)
        }
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoRefreshEnabled(enabled)
            CrashlyticsHelper.setCustomKey("auto_refresh_enabled", enabled)
        }
    }

    fun setImageQuality(quality: ImageQuality) {
        viewModelScope.launch {
            settingsRepository.setImageQuality(quality)
            CrashlyticsHelper.setCustomKey("image_quality", quality.name)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(lang)
            CrashlyticsHelper.setCustomKey("app_language", lang)
        }
    }

    fun setGamingModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setGamingModeEnabled(enabled)
            CrashlyticsHelper.setCustomKey("gaming_mode_enabled", enabled)
        }
    }

    fun setPerformanceModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPerformanceModeEnabled(enabled)
            CrashlyticsHelper.setCustomKey("performance_mode_enabled", enabled)
        }
    }

    fun setShareGamesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShareGamesEnabled(enabled)
            CrashlyticsHelper.setCustomKey("share_games_enabled", enabled)
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkModeEnabled(enabled)
            CrashlyticsHelper.setCustomKey("dark_mode_enabled", enabled)
        }
    }

    // clearDatabase entfernt – Context-Logik gehört in die UI-Schicht
}
