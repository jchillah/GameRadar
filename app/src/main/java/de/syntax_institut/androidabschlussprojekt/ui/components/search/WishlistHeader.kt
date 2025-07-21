package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Kopfzeile für die Wishlist mit Titel und Button zum Leeren der Wunschliste.
 *
 * @param modifier Modifier für das Layout
 * @param hasWishlist Gibt an, ob die Wunschliste Einträge enthält
 * @param onDeleteAllClick Callback für das Löschen der gesamten Wunschliste
 * @param deleteAllContentDescription ContentDescription für den Delete-Button (optional)
 */
@Composable
fun WishlistHeader(
    modifier: Modifier = Modifier,
    hasWishlist: Boolean,
    onDeleteAllClick: () -> Unit,
    deleteAllContentDescription: String? = null,
) {
    val deleteAllContentDescriptionFinal =
        deleteAllContentDescription ?: stringResource(R.string.wishlist_clear_all)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.wishlist_tab),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (hasWishlist) {
            Button(
                onClick = onDeleteAllClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier =
                    Modifier.semantics {
                        contentDescription = deleteAllContentDescriptionFinal
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.dialog_delete_all_favorites_confirm))
            }
        }
    }
    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
}
