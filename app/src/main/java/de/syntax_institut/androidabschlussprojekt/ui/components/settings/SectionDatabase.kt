package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.*

/**
 * Datenbank-Management-Sektion für die Einstellungen.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
@Composable
fun SectionDatabase(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showClearDatabaseDialog by remember { mutableStateOf(false) }
    var showRestartHint by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SettingsButtonItem(
            icon = Icons.Default.DeleteForever,
            title = stringResource(R.string.reset_database_dialog_title),
            subtitle = stringResource(R.string.reset_database_description),
            onClick = { showClearDatabaseDialog = true }
        )

        if (showClearDatabaseDialog) {
            AlertDialog(
                onDismissRequest = { showClearDatabaseDialog = false },
                title = { Text(stringResource(R.string.reset_database_dialog_title)) },
                text = {
                    Text(stringResource(R.string.reset_database_dialog_text))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            GameDatabase.clearDatabase(context)
                            showClearDatabaseDialog = false
                            showRestartHint = true
                        }
                    ) {
                        Text(stringResource(R.string.action_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDatabaseDialog = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
        if (showRestartHint) {
            AlertDialog(
                onDismissRequest = { showRestartHint = false },
                title = { Text(stringResource(R.string.reset_database_dialog_title)) },
                text = { Text(stringResource(R.string.reset_database_restart_hint)) },
                confirmButton = {
                    TextButton(onClick = { showRestartHint = false }) {
                        Text(stringResource(R.string.action_ok))
                    }
                }
            )
        }
    }
} 