package de.syntax_institut.androidabschlussprojekt.ads

import android.content.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import java.io.File
import java.util.Properties

/**
 * Intelligenter AdMob-Manager, der automatisch zwischen Test- und Release-IDs wechselt.
 *
 * Falls echte Werbedaten nicht verfügbar sind (App noch nicht im Store),
 * werden automatisch Test-IDs verwendet.
 */
class AdMobManager(private val context: Context) {

    companion object {
        private const val TAG = "AdMobManager"

        // Test Ad Unit IDs (für Entwicklung und wenn App noch nicht im Store ist)
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

        // Fallback Ad Unit IDs (werden nur verwendet, wenn local.properties nicht verfügbar ist)
        const val FALLBACK_REWARDED_AD_UNIT_ID = "ca-app-pub-7269049262039376/9026235286"
        const val FALLBACK_BANNER_AD_UNIT_ID = "ca-app-pub-7269049262039376/8962672016"
    }

    private var useTestAds = true // Standardmäßig Test-Ads verwenden
    private var hasCheckedReleaseAds = false
    private var forceUseRealAds = false // Neue Variable für erzwungenen Einsatz echter Ads

    /**
     * Liest Ad Unit IDs aus der local.properties Datei
     */
    private fun getAdUnitIdsFromProperties(): Pair<String?, String?> {
        return try {
            val propertiesFile = File(context.filesDir.parentFile?.parentFile, "local.properties")
            if (propertiesFile.exists()) {
                val properties = Properties()
                properties.load(propertiesFile.inputStream())
                
                val rewardedId = properties.getProperty("ADMOB_REWARDED_AD_UNIT_ID")
                val bannerId = properties.getProperty("ADMOB_BANNER_AD_UNIT_ID")
                
                if (rewardedId != null && bannerId != null) {
                    AppLogger.d(TAG, "Found Ad Unit IDs in local.properties")
                    AppLogger.d(TAG, "Rewarded ID: $rewardedId")
                    AppLogger.d(TAG, "Banner ID: $bannerId")
                } else {
                    AppLogger.w(TAG, "Ad Unit IDs not found in local.properties")
                }
                
                Pair(rewardedId, bannerId)
            } else {
                AppLogger.w(TAG, "local.properties file not found")
                Pair(null, null)
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "Error reading local.properties: ${e.message}")
            Pair(null, null)
        }
    }

    /**
     * Erzwingt die Verwendung echter Ads (für Produktion)
     */
    fun forceUseRealAds() {
        forceUseRealAds = true
        useTestAds = false
        hasCheckedReleaseAds = true
        AppLogger.d(TAG, "Forcing use of real ads")
    }

    /**
     * Prüft, ob Release-Ads verfügbar sind und wechselt automatisch.
     * Wird beim ersten Ad-Load aufgerufen.
     */
    fun checkAndSwitchToReleaseAds() {
        if (hasCheckedReleaseAds) return

        // Wenn erzwungen, verwende echte Ads
        if (forceUseRealAds) {
            useTestAds = false
            hasCheckedReleaseAds = true
            AppLogger.d(TAG, "Forced to use real ads")
            return
        }

        // In Debug-Builds immer Test-Ads verwenden
        if (BuildConfig.DEBUG) {
            useTestAds = true
            AppLogger.d(TAG, "Debug build detected - using test ads")
            hasCheckedReleaseAds = true
            return
        }

        // In Release-Builds versuchen wir Release-Ads zu verwenden
        // Falls diese nicht funktionieren, wechseln wir automatisch zu Test-Ads
        useTestAds = false
        hasCheckedReleaseAds = true
        AppLogger.d(TAG, "Release build detected - attempting to use release ads")
    }

    /**
     * Wechselt zu Test-Ads, falls Release-Ads nicht funktionieren.
     */
    fun switchToTestAds() {
        if (useTestAds) return

        useTestAds = true
        AppLogger.w(TAG, "Switching to test ads due to release ad failure")
    }

    /**
     * Gibt die aktuelle Rewarded Ad Unit ID zurück.
     * Priorität: local.properties > Fallback > Test-IDs
     */
    fun getRewardedAdUnitId(): String {
        checkAndSwitchToReleaseAds()
        
        if (useTestAds) {
            AppLogger.d(TAG, "Using test rewarded ad unit ID")
            return TEST_REWARDED_AD_UNIT_ID
        }
        
        // Versuche IDs aus local.properties zu lesen
        val (rewardedId, _) = getAdUnitIdsFromProperties()
        val finalId = rewardedId ?: FALLBACK_REWARDED_AD_UNIT_ID
        
        if (rewardedId != null) {
            AppLogger.d(TAG, "Using real rewarded ad unit ID from local.properties")
        } else {
            AppLogger.w(TAG, "Using fallback rewarded ad unit ID")
        }
        
        return finalId
    }

    /**
     * Gibt die aktuelle Banner Ad Unit ID zurück.
     * Priorität: local.properties > Fallback > Test-IDs
     */
    fun getBannerAdUnitId(): String {
        checkAndSwitchToReleaseAds()
        
        if (useTestAds) {
            AppLogger.d(TAG, "Using test banner ad unit ID")
            return TEST_BANNER_AD_UNIT_ID
        }
        
        // Versuche IDs aus local.properties zu lesen
        val (_, bannerId) = getAdUnitIdsFromProperties()
        val finalId = bannerId ?: FALLBACK_BANNER_AD_UNIT_ID
        
        if (bannerId != null) {
            AppLogger.d(TAG, "Using real banner ad unit ID from local.properties")
        } else {
            AppLogger.w(TAG, "Using fallback banner ad unit ID")
        }
        
        return finalId
    }

    /**
     * Prüft, ob aktuell Test- oder Release-Ads verwendet werden.
     */
    fun isUsingTestAds(): Boolean {
        checkAndSwitchToReleaseAds()
        return useTestAds
    }

    /**
     * Gibt eine benutzerfreundliche Beschreibung des aktuellen Ad-Modus zurück.
     */
    fun getAdModeDescription(): String {
        return if (isUsingTestAds()) {
            "Test-Modus (App noch nicht im Store veröffentlicht)"
        } else {
            // Prüfe, ob echte IDs aus local.properties verwendet werden
            val (rewardedId, bannerId) = getAdUnitIdsFromProperties()
            if (rewardedId != null && bannerId != null) {
                "Release-Modus (Echte Werbung aus local.properties)"
            } else {
                "Release-Modus (Echte Werbung - Fallback IDs)"
            }
        }
    }
} 