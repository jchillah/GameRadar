package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt den aktuellen Pro-Status (werbefrei) als Banner an.
 *
 * - Grüner Haken und positiver Text bei aktivem Pro-Status
 * - Graues Info-Icon und Hinweistext bei nicht aktivem Pro-Status
 * - Optionaler Upgrade-Button (öffnet Kaufdialog)
 * - Barrierefrei, Theme Colors, KDoc
 *
 * @param isProUser true, wenn Pro-Version aktiv
 * @param onUpgradeClick Callback für Upgrade-Button (z.B. Billing-Dialog)
 * @param modifier Modifier für das Layout
 */
@Composable
fun ProStatusBanner(
    isProUser: Boolean,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        if (isProUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isProUser) Color(0xFF43A047) else MaterialTheme.colorScheme.outline
    val textColor =
        if (isProUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val statusText =
        if (isProUser) stringResource(R.string.pro_status_active) else stringResource(R.string.pro_status_inactive)
    val buttonText = stringResource(R.string.pro_status_upgrade)
    val contentDesc =
        if (isProUser) stringResource(R.string.pro_status_active) else stringResource(R.string.pro_status_inactive)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDesc },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isProUser) Icons.Filled.CheckCircle else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
                if (isProUser) {
                    Text(
                        text = stringResource(R.string.pro_status_active_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                } else {
                    Text(
                        text = stringResource(R.string.pro_status_inactive_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
            if (!isProUser) {
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onUpgradeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.semantics { contentDescription = buttonText }
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(buttonText)
                }
            }
        }
    }
}
