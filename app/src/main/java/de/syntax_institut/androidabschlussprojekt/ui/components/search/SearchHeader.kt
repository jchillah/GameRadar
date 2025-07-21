package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Header-Komponente für den Suchbildschirm mit Titel und Filter-Button.
 *
 * Features:
 * - Suchtitel in Headline-Typografie
 * - Filter-Button mit Icon
 * - Responsive Layout mit SpaceBetween
 * - Material3 Design-System
 * - Accessibility-Unterstützung
 * - Einzeilige Textbegrenzung
 *
 * @param modifier Modifier für das Layout
 * @param onFilterClick Callback für Filter-Button-Klicks
 */
@Composable
fun SearchHeader(modifier: Modifier = Modifier, onFilterClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            maxLines = 1,
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = onFilterClick) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = stringResource(R.string.filter_button_content_description)
            )
        }
    }
}
