package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * App-Bar für den Suchbildschirm mit Filter-Button.
 *
 * Features:
 * - Material3 TopAppBar-Design
 * - Suchtitel als Platzhalter
 * - Filter-Button mit Icon
 * - Accessibility-Unterstützung
 * - Responsive Layout
 * - Callback für Filter-Aktionen
 *
 * @param modifier Modifier für das Layout
 * @param onFilterClick Callback für Filter-Button-Klicks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBarRow(
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.search_placeholder)) },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.filter_button_content_description)
                )
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchAppBarRowPreview() {
    SearchAppBarRow(onFilterClick = {})
}