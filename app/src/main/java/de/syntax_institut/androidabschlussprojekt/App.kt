package de.syntax_institut.androidabschlussprojekt

import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private val Context.dataStore by preferencesDataStore(name = "settings")
private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

/**
 * App Composable
 * Hauptcontainer für die gesamte App mit Navigation und Theme.
 */
@Composable
fun App(modifier: Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    // SearchViewModel für automatische Synchronisation
    val searchViewModel: de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel =
        org.koin.androidx.compose.koinViewModel()
    val isOffline by searchViewModel.isOffline.collectAsState()
    var didSync by remember { mutableStateOf(false) }
    var showSyncSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        isDarkTheme = prefs[DARK_MODE_KEY] == true
        isLoaded = true
    }

    // Automatische Synchronisation beim App-Start, nur wenn online und noch nicht synchronisiert
    LaunchedEffect(isOffline, didSync) {
        if (!isOffline && !didSync) {
            searchViewModel.clearCache()
            didSync = true
            showSyncSnackbar = true
        }
    }

    // Snackbar anzeigen, wenn showSyncSnackbar true ist
    LaunchedEffect(showSyncSnackbar) {
        if (showSyncSnackbar) {
            snackbarHostState.showSnackbar("Cache wurde aktualisiert")
            showSyncSnackbar = false
        }
    }

    fun saveDarkMode(enabled: Boolean) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[DARK_MODE_KEY] = enabled
            }
        }
    }

    if (!isLoaded) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    } else {
        MyAppTheme(darkTheme = isDarkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainNavigation(
                        modifier = modifier,
                        isDarkTheme = isDarkTheme,
                        setDarkTheme = {
                            isDarkTheme = it
                            saveDarkMode(it)
                        }
                    )
                    // SnackbarHost für globale Snackbars
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}