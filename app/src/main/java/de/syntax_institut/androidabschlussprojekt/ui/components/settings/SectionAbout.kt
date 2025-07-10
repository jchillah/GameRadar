package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import android.content.*
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.core.net.*
import de.syntax_institut.androidabschlussprojekt.data.*

/**
 * "Über die App"-Sektion für die Einstellungen.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
@Composable
fun SectionAbout(
    modifier: Modifier = Modifier,
    onShowAboutDialog: () -> Unit,
    onShowPrivacyDialog: () -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        SettingsButtonItem(
            icon = Icons.Default.Info,
            title = "Über GameRadar",
            subtitle = "Version 1.0.0",
            onClick = onShowAboutDialog
        )

        SettingsButtonItem(
            icon = Icons.Default.PrivacyTip,
            title = "Datenschutz",
            subtitle = "Datenschutzerklärung lesen",
            onClick = onShowPrivacyDialog
        )

        SettingsButtonItem(
            icon = Icons.Default.Email,
            title = "Support kontaktieren",
            subtitle = Constants.EMAIL,
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:${Constants.EMAIL}".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "Support GameRadar")
                }
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
} 