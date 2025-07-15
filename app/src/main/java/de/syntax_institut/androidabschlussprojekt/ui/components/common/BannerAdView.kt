package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.*
import com.google.android.gms.ads.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Zeigt ein Google AdMob Banner an und tracked Ad-Events (Impression, Klick, Fehler) für Analytics.
 *
 * @param adUnitId Die AdMob AdUnitId (z.B. Test-ID oder echte ID)
 * @param modifier Modifier für das Layout
 * @param analyticsEnabled Analytics-Opt-In Status (nur dann werden Events gesendet)
 */
@Composable
fun BannerAdView(
    adUnitId: String,
    modifier: Modifier = Modifier,
    analyticsEnabled: Boolean = false,
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                adListener =
                    object : AdListener() {
                        override fun onAdImpression() {
                            if (analyticsEnabled)
                                AppAnalytics.trackAdEvent(ctx, "banner", "impression")
                        }

                        override fun onAdClicked() {
                            if (analyticsEnabled)
                                AppAnalytics.trackAdEvent(ctx, "banner", "click")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            if (analyticsEnabled)
                                AppAnalytics.trackAdEvent(ctx, "banner", "error")
                        }
                    }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
