package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.*

/**
 * Composable-Komponente für Feature-Freischaltung (ohne AdMob für Stabilität).
 *
 * Features:
 * - Einfache Feature-Freischaltung ohne AdMob
 * - Loading-Indikator während Freischaltung
 * - Snackbar-Feedback für Belohnungen
 * - Debug-Modus-Unterstützung
 * - Accessibility-Unterstützung
 *
 * Verwendung:
 * - Export-Features freischalten
 * - Statistiken freischalten
 * - Pro-Features temporär verfügbar machen
 *
 * @param adsEnabled Gibt an, ob Werbung aktiviert ist (wird ignoriert für Stabilität)
 * @param modifier Modifier für das Layout
 * @param rewardText Text der Belohnung für den Benutzer
 * @param onReward Callback bei erfolgreicher Freischaltung
 */
@Composable
fun RewardedAdButton(
    adUnitId: String, // Wird ignoriert für Stabilität
    adsEnabled: Boolean, // Wird ignoriert für Stabilität
    modifier: Modifier = Modifier,
    rewardText: String = "Feature freigeschaltet!",
    onReward: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showRewardSnackbar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Strings vorab holen (Composable-Kontext!)
    val adsEnabledText = stringResource(R.string.ads_enabled)

    // Snackbar für Belohnung
    if (showRewardSnackbar) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("$adsEnabledText: $rewardText")
            showRewardSnackbar = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = {
                isLoading = true
                // Simuliere kurze Ladezeit
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    kotlinx.coroutines.delay(1500) // 1.5 Sekunden simulieren
                    isLoading = false
                    showRewardSnackbar = true
                    onReward()
                }
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
}
