package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.theme.*

/**
 * App Composable
 * Verpackt Theme und Navigation in ein Surface.
 */
@Composable
fun App() {
    MyAppTheme(darkTheme = true, dynamicColor = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}