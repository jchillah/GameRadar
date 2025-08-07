package de.syntax_institut.androidabschlussprojekt.ads

import android.content.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Manages loading and showing of rewarded ads.
 *
 * @param context The application context
 */
class RewardedAdManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd = false
    private var loadCallback: com.google.android.gms.ads.rewarded.RewardedAdLoadCallback? = null
    private val adMobManager = AdMobManager(context)

    companion object {
        private const val TAG = "RewardedAdManager"
    }

    /**
     * Loads a rewarded ad asynchronously.
     *
     * @param onAdLoaded Callback when the ad is loaded
     * @param onAdFailedToLoad Callback when the ad fails to load
     */
    fun loadRewardedAd(
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: ((String) -> Unit)? = null,
    ) {
        val adUnitId = adMobManager.getRewardedAdUnitId()
        if (isLoadingAd || isAdAvailable()) {
            AppLogger.d(TAG, "Ad is already loading or loaded")
            return
        }

        isLoadingAd = true

        loadCallback = object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                AppLogger.d(TAG, "Rewarded ad loaded successfully")
                rewardedAd = ad
                isLoadingAd = false
                loadCallback = null
                onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                AppLogger.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")

                // Prüfe, ob es ein "Publisher data not found" Fehler ist
                val errorMessage = loadAdError.message ?: ""
                if (errorMessage.contains("Publisher data not found") ||
                    errorMessage.contains("Account not approved yet")
                ) {
                    // Wechsel zu Test-Ads
                    adMobManager.switchToTestAds()
                    AppLogger.w(TAG, "Switching to test ads due to publisher data not found")

                    // Versuche es erneut mit Test-Ads
                    loadRewardedAd(onAdLoaded, onAdFailedToLoad)
                    return
                }
                
                isLoadingAd = false
                loadCallback = null
                onAdFailedToLoad?.invoke(loadAdError.message ?: "Unknown error")
            }
        }

        RewardedAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            loadCallback as RewardedAdLoadCallback
        )
    }

    /**
     * Shows a rewarded ad if one is available.
     *
     * @param onRewardEarned Callback when the user earns a reward
     * @param onAdDismissed Callback when the ad is dismissed
     * @param onAdFailedToShow Callback when the ad fails to show
     * @return true if the ad was shown, false otherwise
     */
    fun showRewardedAd(
        onRewardEarned: (reward: RewardItem) -> Unit = {},
        onAdDismissed: () -> Unit = {},
        onAdFailedToShow: (String) -> Unit = {},
    ): Boolean {
        val activity = context as? android.app.Activity ?: run {
            onAdFailedToShow("Context is not an Activity")
            return false
        }

        val ad = rewardedAd ?: run {
            onAdFailedToShow("The rewarded ad wasn't ready yet.")
            return false
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                AppLogger.d(TAG, "Ad was dismissed")
                rewardedAd = null
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                AppLogger.e(TAG, "Ad failed to show: ${adError.message}")
                rewardedAd = null
                onAdFailedToShow(adError.message ?: "Unknown error")
            }
        }

        ad.show(activity) { rewardItem ->
            AppLogger.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onRewardEarned(rewardItem)
        }

        return true
    }

    /**
     * Checks if an ad is available to be shown.
     */
    fun isAdAvailable(): Boolean {
        return rewardedAd != null
    }

    /**
     * Preloads a rewarded ad.
     */
    fun preloadAd() {
        loadRewardedAd()
    }

    /**
     * Cleans up resources.
     */
    fun destroy() {
        rewardedAd = null
        loadCallback = null
    }
}
