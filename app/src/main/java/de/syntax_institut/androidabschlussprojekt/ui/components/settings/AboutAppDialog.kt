package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
<<<<<<< Updated upstream
=======
import androidx.compose.ui.res.stringResource
>>>>>>> Stashed changes
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun AboutAppDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.about_app),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
<<<<<<< Updated upstream
                    text = stringResource(R.string.app_version),
=======
                    text = stringResource(R.string.app_name) + " " + stringResource(R.string.app_version),
>>>>>>> Stashed changes
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = stringResource(R.string.about_app_description),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.about_app_features),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
<<<<<<< Updated upstream
                    FeatureItem(stringResource(R.string.about_app_feature_search))
                    FeatureItem(stringResource(R.string.about_app_feature_favorites))
                    FeatureItem(stringResource(R.string.about_app_feature_offline))
                    FeatureItem(stringResource(R.string.about_app_feature_gaming))
                    FeatureItem(stringResource(R.string.about_app_feature_darkmode))
=======
                    FeatureItem(stringResource(R.string.feature_search))
                    FeatureItem(stringResource(R.string.feature_favorites))
                    FeatureItem(stringResource(R.string.feature_offline_cache))
                    FeatureItem(stringResource(R.string.feature_gaming))
                    FeatureItem(stringResource(R.string.feature_dark_mode))
>>>>>>> Stashed changes
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
<<<<<<< Updated upstream
                    text = stringResource(R.string.about_app_community),
=======
                    text = stringResource(R.string.about_app_footer),
>>>>>>> Stashed changes
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutAppDialogPreview() {
    AboutAppDialog(onDismiss = {})
} 