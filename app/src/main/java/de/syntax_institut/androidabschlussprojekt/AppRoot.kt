package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.work.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.services.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.compose.*
import java.util.*
import java.util.concurrent.*

/**
 * AppRoot Composable - Hauptcontainer für die gesamte App mit Navigation und Theme.
 *
 * Features:
 * - Zentrale App-Initialisierung und Konfiguration
 * - Dark Mode-Unterstützung basierend auf Einstellungen
 * - Lokalisierung der gesamten App
 * - WorkManager-Integration für Hintergrundaufgaben
 * - Analytics-Tracking für App-Start
 * - Offline-Banner bei fehlender Internetverbindung
 * - Loading-State während App-Initialisierung
 * - Performance-optimiert für minimale Frame-Drops
 *
 * Funktionalität:
 * - Startet periodischen NewGameWorker für Benachrichtigungen
 * - Überwacht Netzwerkstatus für Offline-Indikator
 * - Integriert alle Hauptkomponenten (Navigation, Theme, Lokalisierung)
 * - Stellt konsistente App-Struktur bereit
 */
@Composable
fun AppRoot() {
    val settingsRepository: SettingsRepository = koinInject()
    val darkModeEnabled by settingsRepository.darkModeEnabled.collectAsState(initial = false)
    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    // Asynchrone Initialisierung für bessere Performance
    LaunchedEffect(Unit) {
        // WorkManager-Initialisierung in Hintergrund
        withContext(Dispatchers.IO) {
            AppAnalytics.trackScreenView("AppStart")
            AppAnalytics.trackEvent("app_started")
            val workManager = WorkManager.getInstance(context)
            val workRequest =
                PeriodicWorkRequestBuilder<NewGameWorker>(6, TimeUnit.HOURS)
                    .addTag(Constants.NEW_GAME_WORKER_NAME)
                    .build()
            workManager.enqueueUniquePeriodicWork(
                Constants.NEW_GAME_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            // RecommendationWorker täglich um 10 Uhr
            val now = Calendar.getInstance()
            val target =
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
                }
            val initialDelay = target.timeInMillis - now.timeInMillis
            val recommendationRequest =
                PeriodicWorkRequestBuilder<RecommendationWorker>(24, TimeUnit.HOURS)
                    .setInitialDelay(
                        initialDelay,
                        java.util.concurrent.TimeUnit.MILLISECONDS
                    )
                    .addTag("RecommendationWorker")
                    .build()
            workManager.enqueueUniquePeriodicWork(
                "RecommendationWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                recommendationRequest
            )
        }
    }

    // Lokalisierung für die gesamte App
    LocalizedApp {
        // Theme wird sofort angewendet
        MyAppTheme(darkTheme = darkModeEnabled) {
            Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    if (!isOnline) {
                        OfflineBanner(
                                isOffline = true,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(WindowInsets.statusBars.asPaddingValues())
                        )
                    }
                    MainNavigation()
                }
            }
        }
    }
}
