package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun WishlistExportImportBar(
    canUseLauncher: Boolean,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (canUseLauncher) {
            Button(onClick = onExport) { Text(stringResource(R.string.wishlist_export)) }
            Button(onClick = onImport) { Text(stringResource(R.string.wishlist_import)) }
        } else {
            Text(
                stringResource(R.string.export_import_preview_unavailable),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
