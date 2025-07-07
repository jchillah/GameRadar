package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.work.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.services.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.compose.*
import java.util.concurrent.*

/**
 * App Composable
 * Hauptcontainer für die gesamte App mit Navigation und Theme.
 */
@Composable
fun App() {
    val settingsRepository: SettingsRepository = koinInject()
    val darkModeEnabled by settingsRepository.darkModeEnabled.collectAsState()
    var isLoaded by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isOnline by NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    LaunchedEffect(Unit) {
        Analytics.trackScreenView("AppStart")
        Analytics.trackEvent("app_started")
        val workManager = WorkManager.getInstance(context)
        val workRequest = PeriodicWorkRequestBuilder<NewGameWorker>(6, TimeUnit.HOURS)
            .addTag("new_game_worker")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "new_game_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    if (!isLoaded) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Loading()
        }
    } else {
        MyAppTheme(darkTheme = darkModeEnabled) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(WindowInsets.statusBars.asPaddingValues())
                ) {
                    if (!isOnline) {
                        OfflineBanner(
                            isOffline = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MainNavigation()
                }
            }
        }
    }
}