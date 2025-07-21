package de.syntax_institut.androidabschlussprojekt.ui.components.wishlist

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
 * Header für die Wunschliste mit Titel und "Wunschliste leeren"-Button.
 *
 * @param hasWishlist Gibt an, ob die Wunschliste Einträge enthält
 * @param onDeleteAllClick Callback für das Löschen der gesamten Wunschliste
 * @param deleteAllContentDescription ContentDescription für den Delete-Button (Barrierefreiheit)
 */
@Composable
fun WishlistHeader(
    hasWishlist: Boolean,
    onDeleteAllClick: () -> Unit,
    deleteAllContentDescription: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.wishlist_tab),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        if (hasWishlist) {
            IconButton(onClick = onDeleteAllClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = deleteAllContentDescription
                )
            }
        }
    }
}