package de.syntax_institut.androidabschlussprojekt.ads

import android.content.*
import com.google.android.gms.ads.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Manages banner ads for the app.
 *
 * @param context The application context
 */
class BannerAdManager(private val context: Context) {

    companion object {
        private const val TAG = "BannerAdManager"
    }

    private val adMobManager = AdMobManager(context)
    
    /**
     * Creates and configures a banner ad view.
     *
     * @return Configured AdView
     */
    fun createBannerAd(): AdView {
        val adUnitId = adMobManager.getBannerAdUnitId()
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId

            // Load the ad
            loadAd(AdRequest.Builder().build())

            AppLogger.d(TAG, "Banner ad created and loading: $adUnitId")
        }
    }

    /**
     * Creates a banner ad with custom size.
     *
     * @param adSize The desired ad size
     * @return Configured AdView
     */
    fun createSizedBannerAd(adSize: AdSize): AdView {
        val adUnitId = adMobManager.getBannerAdUnitId()
        return AdView(context).apply {
            setAdSize(adSize)
            this.adUnitId = adUnitId

            // Load the ad
            loadAd(AdRequest.Builder().build())

            AppLogger.d(TAG, "Banner ad created with custom size: $adSize")
        }
    }
} 