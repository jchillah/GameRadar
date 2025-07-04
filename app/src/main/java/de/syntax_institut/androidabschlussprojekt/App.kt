package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import org.koin.compose.*

/**
 * App Composable
 * Hauptcontainer für die gesamte App mit Navigation und Theme.
 */
@Composable
fun AppStart(modifier: Modifier) {
    val settingsRepository: SettingsRepository = koinInject()
    val darkModeEnabled by settingsRepository.darkModeEnabled.collectAsState()
    var isLoaded by remember { mutableStateOf(true) }

    if (!isLoaded) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        MyAppTheme(darkTheme = darkModeEnabled) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainNavigation(
                    modifier = modifier
                )
            }
        }
    }
}