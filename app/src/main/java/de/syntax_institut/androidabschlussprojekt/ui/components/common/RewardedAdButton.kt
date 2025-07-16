package de.syntax_institut.androidabschlussprojekt.ui.components.common

import android.app.*
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
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.utils.*

/**
 * Button zum freiwilligen Ansehen eines Rewarded Ads (Belohnungsvideo).
 *
 * - Zeigt nur, wenn Werbung erlaubt (Opt-In) und keine Pro-Version.
 * - Lädt und zeigt das Rewarded Ad über den RewardedAdManager.
 * - Zeigt Snackbar-Feedback nach Abschluss oder Fehler.
 * - Nutzt Theme Colors, ist barrierefrei und KDoc-dokumentiert.
 *
 * @param adUnitId Die AdMob Rewarded AdUnitId
 * @param adsEnabled Opt-In für Werbung
 * @param isProUser Pro-Status (keine Werbung bei Pro)
 * @param modifier Modifier für das Layout
 * @param rewardText Text, der die Belohnung beschreibt
 * @param onReward Callback, wenn Nutzer die Belohnung erhält
 */
@Composable
fun RewardedAdButton(
    adUnitId: String,
    adsEnabled: Boolean,
    isProUser: Boolean,
    modifier: Modifier = Modifier,
    rewardText: String = "1 Tag werbefrei als Belohnung!",
    onReward: () -> Unit = {},
) {
    // Button nur anzeigen, wenn Werbung erlaubt und kein Pro-User, oder im Debug-Build
    val isDebug = BuildConfig.DEBUG
    if ((!adsEnabled || isProUser) && !isDebug) return
    val context = LocalContext.current
    val activity = context as? Activity ?: return
    val isLoading by RewardedAdManager.isLoading.collectAsState()
    val error by RewardedAdManager.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showRewardSnackbar by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var shouldShowAd by remember { mutableStateOf(false) }

    // Strings vorab holen (Composable-Kontext!)
    val adsEnabledText = stringResource(R.string.ads_enabled)
    val unknownErrorText = stringResource(R.string.error_unknown)

    // Snackbar für Belohnung
    if (showRewardSnackbar) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("$adsEnabledText: $rewardText")
            showRewardSnackbar = false
        }
    }
    // Snackbar für Fehler
    if (showErrorSnackbar && error != null) {
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error ?: unknownErrorText)
            showErrorSnackbar = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = {
                RewardedAdManager.loadAd(context, adUnitId, adsEnabled, isProUser)
                shouldShowAd = true
            },
            enabled = !isLoading,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .semantics {
                        contentDescription = adsEnabledText
                    },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("$adsEnabledText – $rewardText")
        }
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        // SnackbarHost für Feedback
        SnackbarHost(hostState = snackbarHostState)
    }
    // Zeige das Ad, wenn geladen und shouldShowAd true ist
    LaunchedEffect(isLoading, shouldShowAd) {
        if (!isLoading && shouldShowAd) {
            RewardedAdManager.showAdIfAvailable(
                activity = activity,
                onReward = {
                    showRewardSnackbar = true
                    onReward()
                },
                onClosed = {
                    RewardedAdManager.loadAd(context, adUnitId, adsEnabled, isProUser)
                }
            )
            shouldShowAd = false
        }
    }
    // Fehlerbehandlung
    LaunchedEffect(error) { if (error != null) showErrorSnackbar = true }
}
