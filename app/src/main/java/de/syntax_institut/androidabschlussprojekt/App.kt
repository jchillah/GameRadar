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
import org.koin.compose.*
import java.util.concurrent.*

/** AppRoot Composable Hauptcontainer für die gesamte App mit Navigation und Theme. */
@Composable
fun AppRoot() {
    val settingsRepository: SettingsRepository = koinInject()
    val darkModeEnabled by settingsRepository.darkModeEnabled.collectAsState()
    var isLoaded by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isOnline by
    NetworkUtils.observeNetworkStatus(context)
        .collectAsState(initial = NetworkUtils.isNetworkAvailable(context))

    LaunchedEffect(Unit) {
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
    }

    if (!isLoaded) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Loading()
        }
    } else {
        LocalizedApp {
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
                                        .padding(
                                            WindowInsets.statusBars
                                                .asPaddingValues()
                                        )
                            )
                        }
                        MainNavigation()
                    }
                }
            }
        }
    }
}
