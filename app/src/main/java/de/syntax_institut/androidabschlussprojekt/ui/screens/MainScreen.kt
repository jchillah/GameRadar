package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Suche") },
                    label = { Text("Suche") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoriten") },
                    label = { Text("Favoriten") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        // Hier zeigen wir den aktuellen Screen basierend auf dem ausgewählten Tab an
        when (selectedTab) {
            0 -> SearchScreen(navController, modifier = Modifier.padding(innerPadding))
            1 -> FavoritesScreen(navController, modifier = Modifier.padding(innerPadding))
            else -> SearchScreen(navController, modifier = Modifier.padding(innerPadding)) // Default
        }
    }
} 