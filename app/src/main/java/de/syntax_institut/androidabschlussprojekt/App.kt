package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import de.syntax_institut.androidabschlussprojekt.navigation.NavGraph
import de.syntax_institut.androidabschlussprojekt.ui.theme.AppTheme

/**
 * App Composable
 * Verpackt Theme und Navigation in ein Surface.
 */
@Composable
fun App() {
    AppTheme(darkTheme = true, dynamicColor = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}