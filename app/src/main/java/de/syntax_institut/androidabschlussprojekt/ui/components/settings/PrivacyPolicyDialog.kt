package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

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
                    text = "Datenschutzerklärung",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "GameFinder Datenschutzerklärung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Stand: ${java.time.LocalDate.now()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PrivacySection(
                    title = "1. Datenerhebung",
                    content = "GameFinder sammelt nur die Daten, die für die Funktionalität der App notwendig sind. " +
                            "Dazu gehören deine Suchanfragen, Favoriten und App-Einstellungen, die lokal auf deinem Gerät gespeichert werden."
                )

                PrivacySection(
                    title = "2. Externe Dienste",
                    content = "Die App nutzt die RAWG-API für Spieldaten. Diese API sammelt keine persönlichen Daten von dir. " +
                            "Alle API-Anfragen erfolgen anonym ohne persönliche Identifikation."
                )

                PrivacySection(
                    title = "3. Lokale Speicherung",
                    content = "Deine Favoriten, Suchverlauf und Einstellungen werden ausschließlich lokal auf deinem Gerät gespeichert. " +
                            "Diese Daten werden nicht an externe Server übertragen."
                )

                PrivacySection(
                    title = "4. Cache-Daten",
                    content = "Die App speichert Spieldaten im lokalen Cache, um die Performance zu verbessern. " +
                            "Diese Daten können jederzeit über die Einstellungen gelöscht werden."
                )

                PrivacySection(
                    title = "5. Analytics",
                    content = "GameFinder sammelt anonyme Nutzungsdaten zur Verbesserung der App. " +
                            "Diese Daten enthalten keine persönlichen Informationen und werden nur für interne Zwecke verwendet."
                )

                PrivacySection(
                    title = "6. Deine Rechte",
                    content = "Du hast das Recht, alle deine Daten zu löschen, indem du die App-Einstellungen verwendest. " +
                            "Alle Daten werden lokal gespeichert und können vollständig entfernt werden."
                )

                Text(
                    text = "Bei Fragen zum Datenschutz kontaktiere uns unter: privacy@gamefinder.app",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Verstanden")
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