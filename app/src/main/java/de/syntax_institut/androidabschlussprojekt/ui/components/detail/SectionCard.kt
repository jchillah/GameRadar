package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Card-Komponente für Sektionen im Detail-Screen.
 *
 * Zeigt einen Titel und beliebigen Inhalt in einer Card mit Padding an.
 *
 * @param title Titel der Sektion
 * @param content Composable-Content für die Sektion
 */
@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column{
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SectionCardPreview() {
    SectionCard(title = stringResource(R.string.preview_description)) {
        Text(stringResource(R.string.preview_sectioncard_content))
    }
}
