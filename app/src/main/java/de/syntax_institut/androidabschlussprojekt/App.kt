package de.syntax_institut.androidabschlussprojekt

import android.content.*
import android.util.*
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
fun AppStart(modifier: Modifier) {
    val context = LocalContext.current
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            // Verzögerung für stabilen App-Start
            delay(50)
            val prefs = context.dataStore.data.first()
            isDarkTheme = prefs[DARK_MODE_KEY] == true
        } catch (e: Exception) {
            // Fallback auf Light Theme bei Fehlern
            Log.w("AppStart", "Fehler beim Laden der Theme-Einstellungen", e)
            isDarkTheme = false
        } finally {
            isLoaded = true
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
                MainNavigation(
                    modifier = modifier
                )
            }
        }
    }
}