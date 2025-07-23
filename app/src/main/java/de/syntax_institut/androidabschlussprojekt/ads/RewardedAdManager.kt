package de.syntax_institut.androidabschlussprojekt.ads

import android.content.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Manages loading and showing of rewarded ads.
 *
 * @param context The application context
 * @param testAdUnitId Optional test ad unit ID for development
 */
class RewardedAdManager(
    private val context: Context,
    private val testAdUnitId: String? = null,
) {
    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd = false
    private var loadCallback: com.google.android.gms.ads.rewarded.RewardedAdLoadCallback? = null

    companion object {
        private const val TAG = "RewardedAdManager"

        // Test ad unit ID - replace with your actual ad unit ID in production
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    /**
     * Loads a rewarded ad asynchronously.
     *
     * @param adUnitId The ad unit ID to load the ad from
     * @param onAdLoaded Callback when the ad is loaded
     * @param onAdFailedToLoad Callback when the ad fails to load
     */
    fun loadRewardedAd(
        adUnitId: String = testAdUnitId ?: TEST_REWARDED_AD_UNIT_ID,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailedToLoad: ((String) -> Unit)? = null,
    ) {
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
    fun preloadAd(adUnitId: String = testAdUnitId ?: TEST_REWARDED_AD_UNIT_ID) {
        loadRewardedAd(adUnitId)
    }

    /**
     * Cleans up resources.
     */
    fun destroy() {
        rewardedAd = null
        loadCallback = null
    }
}
