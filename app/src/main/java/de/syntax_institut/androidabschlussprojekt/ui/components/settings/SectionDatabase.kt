package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import de.syntax_institut.androidabschlussprojekt.data.*
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

    Column(modifier = modifier) {
        SettingsButtonItem(
            icon = Icons.Default.DeleteForever,
            title = Constants.DIALOG_RESET_DATABASE_TITLE,
            subtitle = Constants.DIALOG_RESET_DATABASE_SUBTITLE,
            onClick = { showClearDatabaseDialog = true }
        )

        if (showClearDatabaseDialog) {
            AlertDialog(
                onDismissRequest = { showClearDatabaseDialog = false },
                title = { Text(Constants.DIALOG_RESET_DATABASE_TITLE) },
                text = {
                    Text(Constants.DIALOG_RESET_DATABASE_TEXT)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            GameDatabase.clearDatabase(context)
                            showClearDatabaseDialog = false
                        }
                    ) {
                        Text(Constants.DIALOG_DELETE_ALL_CONFIRM)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDatabaseDialog = false }) {
                        Text(Constants.DIALOG_DELETE_ALL_CANCEL)
                    }
                }
            )
        }
    }
} 