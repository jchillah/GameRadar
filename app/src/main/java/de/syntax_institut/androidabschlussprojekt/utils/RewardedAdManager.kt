package de.syntax_institut.androidabschlussprojekt.utils

import android.app.*
import android.content.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.*
import kotlinx.coroutines.flow.*

/**
 * Singleton-Manager für Rewarded Ads (Belohnungsanzeigen).
 *
 * - Lädt und zeigt Rewarded Ads nur, wenn Opt-In für Werbung aktiv und keine Pro-Version.
 * - Sendet Analytics-Events (Impression, Klick, Fehler, Reward).
 * - KDoc und Clean Code.
 */
object RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Lädt eine neue Rewarded Ad, wenn Opt-In aktiv und keine Pro-Version.
     * @param context Context
     * @param adUnitId Die AdMob Rewarded AdUnitId
     * @param adsEnabled Opt-In für Werbung
     * @param isProUser Pro-Status
     */
    fun loadAd(context: Context, adUnitId: String, adsEnabled: Boolean, isProUser: Boolean) {
        if (!adsEnabled || isProUser) return
        if (rewardedAd != null) return // Bereits geladen
        _isLoading.value = true
        _error.value = null
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _isLoading.value = false
                    AppAnalytics.trackAdEvent(context, "rewarded", "loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    _isLoading.value = false
                    _error.value = loadAdError.message
                    AppAnalytics.trackAdEvent(context, "rewarded", "error")
                }
            }
        )
    }

    /**
     * Zeigt die Rewarded Ad an, falls verfügbar. Belohnung wird per Callback signalisiert.
     * @param activity Activity
     * @param onReward Callback, wenn Nutzer die Belohnung erhält
     * @param onClosed Optionaler Callback, wenn Ad geschlossen wurde
     */
    fun showAdIfAvailable(
        activity: Activity,
        onReward: () -> Unit,
        onClosed: (() -> Unit)? = null,
    ) {
        val ad = rewardedAd ?: return
        rewardedAd = null // Ad kann nur einmal gezeigt werden
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                AppAnalytics.trackAdEvent(activity, "rewarded", "impression")
            }

            override fun onAdClicked() {
                AppAnalytics.trackAdEvent(activity, "rewarded", "click")
            }

            override fun onAdDismissedFullScreenContent() {
                onClosed?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                AppAnalytics.trackAdEvent(activity, "rewarded", "error")
                _error.value = adError.message
                onClosed?.invoke()
            }
        }
        ad.show(activity) { rewardItem: RewardItem ->
            AppAnalytics.trackAdEvent(activity, "rewarded", "reward")
            onReward()
        }
    }

    /**
     * Setzt den Manager zurück (z. B. nach Fehler oder bei Logout).
     */
    fun reset() {
        rewardedAd = null
        _isLoading.value = false
        _error.value = null
    }
} 