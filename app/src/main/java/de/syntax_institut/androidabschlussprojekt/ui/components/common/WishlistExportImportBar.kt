package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Export/Import-Bar für Wunschlisten mit bedingter Anzeige.
 *
 * Zeigt Export- und Import-Buttons an, wenn Launcher verfügbar sind,
 * andernfalls eine Informationsnachricht.
 *
 * @param canUseLauncher Gibt an, ob ActivityResultLauncher verfügbar sind
 * @param onExport Callback für Export-Aktion
 * @param onImport Callback für Import-Aktion
 * @param modifier Modifier für das Layout
 */
@Composable
fun WishlistExportImportBar(
    canUseLauncher: Boolean,
    onExport: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (canUseLauncher) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onExport) { Text(stringResource(R.string.wishlist_export)) }
            Button(onClick = onImport) { Text(stringResource(R.string.wishlist_import)) }
        }
    } else {
        Text(
            stringResource(R.string.export_import_preview_unavailable),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}
