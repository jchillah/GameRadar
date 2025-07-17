package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.favorites.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

/**
 * Screen zur Anzeige von Spielstatistiken (z.B. Genre-Verteilung) basierend auf den Favoriten.
 *
 * @param navController Navigation Controller für die Navigation
 * @param viewModel ViewModel für Favoriten (liefert die Statistikdaten)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavHostController, viewModel: FavoritesViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val genreCounts = state.favorites.flatMap { it.genres }.groupingBy { it }.eachCount()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiken") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier
            .padding(padding)
            .fillMaxSize()) {
            GameStatsChart(genreCounts = genreCounts, modifier = Modifier.fillMaxWidth())
            // Weitere Statistiken können hier ergänzt werden
        }
    }
}
