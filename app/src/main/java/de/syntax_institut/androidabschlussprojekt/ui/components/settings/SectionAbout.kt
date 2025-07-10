package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import android.app.*
import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import de.syntax_institut.androidabschlussprojekt.R
import androidx.core.net.toUri

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
    val email = stringResource(R.string.email)
    val subject = stringResource(R.string.contact_support_subject)

    Column(modifier = modifier) {
        SettingsButtonItem(
            icon = Icons.Default.Info,
            title = stringResource(R.string.about_app),
            subtitle = stringResource(R.string.app_version),
            onClick = onShowAboutDialog
        )

        SettingsButtonItem(
            icon = Icons.Default.PrivacyTip,
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.privacy_policy_description),
            onClick = onShowPrivacyDialog
        )

        SettingsButtonItem(
            icon = Icons.Default.Email,
            title = stringResource(R.string.contact_support),
            subtitle = email,
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:$email".toUri()
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                }
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
} 