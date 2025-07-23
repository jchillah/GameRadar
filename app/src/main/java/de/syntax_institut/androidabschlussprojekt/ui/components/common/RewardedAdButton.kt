package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ads.*
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Composable component that shows a button to display a rewarded ad.
 * When clicked, it loads and shows a rewarded ad, and calls the onReward callback
 * when the user successfully watches the ad.
 *
 * @param adUnitId The ad unit ID for the rewarded ad
 * @param adsEnabled Whether ads are enabled in the app
 * @param modifier Modifier for the button
 * @param rewardText Text to show when the reward is granted
 * @param onReward Callback when the user successfully watches the ad
 * @param onAdDismissed Callback when the ad is dismissed
 * @param onAdFailedToLoad Callback when the ad fails to load
 * @param onAdFailedToShow Callback when the ad fails to show
 */
@Composable
fun RewardedAdButton(
    adUnitId: String,
    adsEnabled: Boolean,
    modifier: Modifier = Modifier,
    rewardText: String = "Feature freigeschaltet!",
    onReward: () -> Unit = {},
    onAdDismissed: () -> Unit = {},
    onAdFailedToLoad: (String) -> Unit = {},
    onAdFailedToShow: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showRewardSnackbar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Create or get the RewardedAdManager
    val rewardedAdManager = remember {
        RewardedAdManager(context, adUnitId.takeIf { it.isNotBlank() })
    }

    // Strings
    val adsEnabledText = stringResource(R.string.ads_enabled)
    val watchAdText = stringResource(R.string.watch_ad)
    val loadingText = stringResource(R.string.loading)
    val adFailedText = stringResource(R.string.ad_failed)
    val tryAgainText = stringResource(R.string.try_again)

    // Handle ad loading and showing
    LaunchedEffect(adUnitId, adsEnabled) {
        if (adsEnabled) {
            isLoading = true
            try {
                // Preload the ad when the component is first composed
                rewardedAdManager.loadRewardedAd(
                    adUnitId = adUnitId,
                    onAdLoaded = {
                        isLoading = false
                        errorMessage = null
                    },
                    onAdFailedToLoad = { error ->
                        isLoading = false
                        errorMessage = error
                        onAdFailedToLoad(error)
                    }
                )
            } catch (e: Exception) {
                AppLogger.e("RewardedAdButton", "Error loading ad: ${e.message}")
                errorMessage = e.message
                isLoading = false
                onAdFailedToLoad(e.message ?: "Unknown error")
            }
        }
    }

    // Show reward snackbar
    if (showRewardSnackbar) {
        LaunchedEffect(showRewardSnackbar) {
            snackbarHostState.showSnackbar(
                message = rewardText,
                duration = SnackbarDuration.Short
            )
            showRewardSnackbar = false
        }
    }

    // Show error snackbar
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = "$adFailedText: $message",
                duration = SnackbarDuration.Long,
                actionLabel = tryAgainText
            )
            errorMessage = null
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button to show the rewarded ad
        Button(
            onClick = {
                if (!adsEnabled) {
                    // If ads are disabled, just trigger the reward
                    onReward()
                    showRewardSnackbar = true
                } else if (rewardedAdManager.isAdAvailable()) {
                    // Show the ad if it's loaded
                    val success = rewardedAdManager.showRewardedAd(
                        onRewardEarned = { reward ->
                            AppLogger.d(
                                "RewardedAdButton",
                                "Reward earned: ${reward.amount} ${reward.type}"
                            )
                            onReward()
                        },
                        onAdDismissed = {
                            onAdDismissed()
                        },
                        onAdFailedToShow = { error ->
                            onAdFailedToShow(error)
                            errorMessage = error
                        }
                    )

                    if (!success) {
                        errorMessage = "Failed to show ad"
                    }
                } else {
                    // If ad is not loaded, try to load it
                    isLoading = true
                    rewardedAdManager.loadRewardedAd(
                        adUnitId = adUnitId,
                        onAdLoaded = {
                            isLoading = false
                            // Retry showing the ad after it's loaded
                            // This will be handled in the next recomposition
                        },
                        onAdFailedToLoad = { error ->
                            isLoading = false
                            errorMessage = error
                            onAdFailedToLoad(error)
                        }
                    )
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = if (adsEnabled) {
                        "Belohnung durch Ansehen einer Werbung freischalten"
                    } else {
                        "Belohnung freischalten"
                    }
                }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(loadingText)
            } else {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(if (adsEnabled) watchAdText else "Belohnung freischalten")
            }
        }

        // Debug info for ad status
        if (adsEnabled) {
            Text(
                text = "Export: $adsEnabledText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Snackbar host for notifications
        SnackbarHost(hostState = snackbarHostState)
    }

    // Clean up when the component is disposed
    DisposableEffect(Unit) {
        onDispose {
            rewardedAdManager.destroy()
        }
    }
}
