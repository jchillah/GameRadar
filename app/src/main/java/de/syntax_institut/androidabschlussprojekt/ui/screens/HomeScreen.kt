package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToSettings: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Home", color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                text = "Go to Settings",
                onClick = onNavigateToSettings,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
