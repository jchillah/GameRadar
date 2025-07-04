package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBarRow(
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit,
) {
    TopAppBar(
        title = { Text("Spielsuche") },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter anzeigen"
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