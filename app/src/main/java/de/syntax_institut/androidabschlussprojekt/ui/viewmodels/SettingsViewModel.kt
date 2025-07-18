package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.lifecycle.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.flow.*

/** ViewModel für die Einstellungen. Stellt alle Settings-States als StateFlow bereit. */
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

    /**
     * Gibt an, ob der Nutzer Pro-Status (zahlender, werbefreier User) ist. Sollte aus den
     * Einstellungen oder einer Billing-Logik kommen. Hier als Platzhalter: Immer false (kein Pro),
     * kann später dynamisch gesetzt werden.
     */
    private val _proStatus = MutableStateFlow(false)
    val proStatus: StateFlow<Boolean> = _proStatus.asStateFlow()

    /** Setzt den Pro-Status (z.B. nach Kauf oder Restore). */
    fun setProStatus(isPro: Boolean) {
        _proStatus.value = isPro
    }

    /** Gibt an, ob Werbung (AdMob) angezeigt werden darf (Opt-In). */
    val adsEnabled =
        settingsRepository.adsEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            settingsRepository.adsEnabled.value
        )

    /** Setzt, ob Werbung (AdMob) angezeigt werden darf (Opt-In). */
    fun setAdsEnabled(enabled: Boolean) {
        settingsRepository.setAdsEnabled(enabled)
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        settingsRepository.setAnalyticsEnabled(enabled)
        AppAnalytics.setAnalyticsEnabled(enabled)
        CrashlyticsHelper.setCrashlyticsEnabled(enabled)
        CrashlyticsHelper.setCustomKey("analytics_enabled", enabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
        CrashlyticsHelper.setCustomKey("notifications_enabled", enabled)
    }

    fun setAutoRefreshEnabled(enabled: Boolean) {
        settingsRepository.setAutoRefreshEnabled(enabled)
        CrashlyticsHelper.setCustomKey("auto_refresh_enabled", enabled)
    }

    fun setImageQuality(quality: ImageQuality) {
        settingsRepository.setImageQuality(quality)
        CrashlyticsHelper.setCustomKey("image_quality", quality.name)
    }

    fun setLanguage(lang: String) {
        settingsRepository.setLanguage(lang)
        CrashlyticsHelper.setCustomKey("app_language", lang)
    }

    fun setGamingModeEnabled(enabled: Boolean) {
        settingsRepository.setGamingModeEnabled(enabled)
        CrashlyticsHelper.setCustomKey("gaming_mode_enabled", enabled)
    }

    fun setPerformanceModeEnabled(enabled: Boolean) {
        settingsRepository.setPerformanceModeEnabled(enabled)
        CrashlyticsHelper.setCustomKey("performance_mode_enabled", enabled)
    }

    fun setShareGamesEnabled(enabled: Boolean) {
        settingsRepository.setShareGamesEnabled(enabled)
        CrashlyticsHelper.setCustomKey("share_games_enabled", enabled)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        settingsRepository.setDarkModeEnabled(enabled)
        CrashlyticsHelper.setCustomKey("dark_mode_enabled", enabled)
    }
}
