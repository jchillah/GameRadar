package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val notificationsEnabled = settingsRepository.notificationsEnabled
    val autoRefreshEnabled = settingsRepository.autoRefreshEnabled
    val imageQuality = settingsRepository.imageQuality
    val language = settingsRepository.language
    val gamingModeEnabled = settingsRepository.gamingModeEnabled
    val performanceModeEnabled = settingsRepository.performanceModeEnabled
    val shareGamesEnabled = settingsRepository.shareGamesEnabled
    val darkModeEnabled = settingsRepository.darkModeEnabled

    fun setNotificationsEnabled(enabled: Boolean) =
        settingsRepository.setNotificationsEnabled(enabled)

    fun setAutoRefreshEnabled(enabled: Boolean) = settingsRepository.setAutoRefreshEnabled(enabled)
    fun setImageQuality(quality: ImageQuality) = settingsRepository.setImageQuality(quality)
    fun setLanguage(lang: String) = settingsRepository.setLanguage(lang)
    fun setGamingModeEnabled(enabled: Boolean) = settingsRepository.setGamingModeEnabled(enabled)
    fun setPerformanceModeEnabled(enabled: Boolean) =
        settingsRepository.setPerformanceModeEnabled(enabled)

    fun setShareGamesEnabled(enabled: Boolean) = settingsRepository.setShareGamesEnabled(enabled)
    fun setDarkModeEnabled(enabled: Boolean) = settingsRepository.setDarkModeEnabled(enabled)

    // clearDatabase entfernt – Context-Logik gehört in die UI-Schicht
}