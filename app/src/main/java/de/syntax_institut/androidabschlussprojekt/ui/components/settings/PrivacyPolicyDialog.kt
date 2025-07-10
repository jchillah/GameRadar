package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.*
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
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.privacy_policy_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
<<<<<<< Updated upstream
                    text = stringResource(
                        R.string.privacy_policy_date,
                        java.time.LocalDate.now().toString()
                    ),
=======
                    text = stringResource(R.string.privacy_policy_date, java.time.LocalDate.now().toString()),
>>>>>>> Stashed changes
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_1_title),
                    content = stringResource(R.string.privacy_section_1_content)
=======
                    title = stringResource(R.string.privacy_section1_title),
                    content = stringResource(R.string.privacy_section1_content)
>>>>>>> Stashed changes
                )
                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_2_title),
                    content = stringResource(R.string.privacy_section_2_content)
=======
                    title = stringResource(R.string.privacy_section2_title),
                    content = stringResource(R.string.privacy_section2_content)
>>>>>>> Stashed changes
                )
                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_3_title),
                    content = stringResource(R.string.privacy_section_3_content)
=======
                    title = stringResource(R.string.privacy_section3_title),
                    content = stringResource(R.string.privacy_section3_content)
>>>>>>> Stashed changes
                )
                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_4_title),
                    content = stringResource(R.string.privacy_section_4_content)
=======
                    title = stringResource(R.string.privacy_section4_title),
                    content = stringResource(R.string.privacy_section4_content)
>>>>>>> Stashed changes
                )
                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_5_title),
                    content = stringResource(R.string.privacy_section_5_content)
=======
                    title = stringResource(R.string.privacy_section5_title),
                    content = stringResource(R.string.privacy_section5_content)
>>>>>>> Stashed changes
                )
                PrivacySection(
<<<<<<< Updated upstream
                    title = stringResource(R.string.privacy_section_6_title),
                    content = stringResource(R.string.privacy_section_6_content)
=======
                    title = stringResource(R.string.privacy_section6_title),
                    content = stringResource(R.string.privacy_section6_content)
>>>>>>> Stashed changes
                )
                Text(
<<<<<<< Updated upstream
                    text = stringResource(
                        R.string.privacy_policy_contact,
                        "michael.winkler.developer@gmail.com"
                    ),
=======
                    text = stringResource(R.string.privacy_policy_contact, "michael.winkler.developer@gmail.com"),
>>>>>>> Stashed changes
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
<<<<<<< Updated upstream
                Text(stringResource(R.string.action_ok))
=======
                Text(stringResource(R.string.action_understood))
>>>>>>> Stashed changes
            }
        }
    )
}

@Composable
private fun PrivacySection(
    title: String,
    content: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyDialogPreview() {
    PrivacyPolicyDialog(onDismiss = {})
} 