package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil3.compose.*
import coil3.request.*
import coil3.size.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*

/**
 * Einzelnes Spiel-Item für Listen und Suchergebnisse.
 *
 * Features:
 * - Spielbild mit anpassbarer Qualität und Loading-States
 * - Spieltitel, Release-Datum und Bewertung
 * - Favoriten-Status mit Herz-Icon
 * - Wunschlisten-Status mit Stern-Icon
 * - Löschen-Button für Favoriten/Wunschliste
 * - Material3 Card-Design
 * - Responsive Layout
 * - Accessibility-Unterstützung
 * - Crossfade-Animation für Bilder
 *
 * @param game Das anzuzeigende Spiel
 * @param onClick Callback beim Klick auf das Spiel
 * @param onDelete Optionaler Callback zum Löschen des Spiels
 * @param imageQuality Qualitätseinstellung für das Spielbild
 * @param isFavorite Gibt an, ob das Spiel in den Favoriten ist
 * @param showFavoriteIcon Gibt an, ob das Favoriten-Icon angezeigt werden soll
 * @param isInWishlist Gibt an, ob das Spiel in der Wunschliste ist
 * @param onWishlistChanged Optionaler Callback bei Wunschlisten-Änderung
 * @param showWishlistButton Gibt an, ob der Wunschlisten-Button angezeigt werden soll
 */
@Composable
fun GameItem(
        game: Game,
        onClick: () -> Unit,
        onDelete: (() -> Unit)? = null,
        imageQuality: ImageQuality = ImageQuality.HIGH,
        isFavorite: Boolean = false,
        showFavoriteIcon: Boolean = true,
        isInWishlist: Boolean = false,
        onWishlistChanged: ((Boolean) -> Unit)? = null,
        showWishlistButton: Boolean = true,
) {
        val context = LocalContext.current

        Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onClick() },
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        val size =
                                when (imageQuality) {
                                        ImageQuality.LOW -> Size(160, 90)
                                        ImageQuality.MEDIUM -> Size(320, 180)
                                        ImageQuality.HIGH -> Size.ORIGINAL
                                }
                        SubcomposeAsyncImage(
                                model =
                                        ImageRequest.Builder(context)
                                                .data(game.imageUrl)
                                                .size(size)
                                                .crossfade(true)
                                                .build(),
                                contentDescription = game.title,
                                contentScale = ContentScale.Crop,
                                modifier =
                                        Modifier
                                            .width(120.dp)
                                            .aspectRatio(16f / 9f)
                                            .clip(MaterialTheme.shapes.small)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                loading = {
                                        Box(
                                                modifier =
                                                        Modifier
                                                            .width(120.dp)
                                                            .aspectRatio(16f / 9f)
                                                            .clip(MaterialTheme.shapes.small),
                                                contentAlignment = Alignment.Center
                                        ) { Loading(modifier = Modifier.size(24.dp)) }
                                },
                                error = {
                                        Box(
                                                modifier =
                                                        Modifier
                                                            .width(120.dp)
                                                            .aspectRatio(16f / 9f)
                                                            .clip(MaterialTheme.shapes.small)
                                                            .background(
                                                                MaterialTheme.colorScheme
                                                                    .surfaceVariant
                                                            ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.BrokenImage,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        modifier = Modifier.size(32.dp)
                                                )
                                        }
                                }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = game.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            Spacer(modifier = Modifier.height(4.dp))
                                game.releaseDate?.let {
                                        Text(
                                                text =
                                                        stringResource(
                                                                R.string
                                                                        .game_release_date_search_gameitem,
                                                                it
                                                        ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                            Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                        text =
                                                stringResource(
                                                        R.string.game_rating_search_gameitem,
                                                        game.rating
                                                ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                    // Action-Buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Herz-Icon für Favoritenstatus
                        if (showFavoriteIcon) {
                            Icon(
                                imageVector =
                                    if (isFavorite) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder,
                                contentDescription =
                                    if (isFavorite)
                                        stringResource(R.string.favorite_marked)
                                    else stringResource(R.string.favorite_not_marked),
                                tint =
                                    if (isFavorite) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Wishlist-Button (Stern)
                        if (showWishlistButton && onWishlistChanged != null) {
                            WishlistButton(
                                isInWishlist = isInWishlist,
                                onWishlistChanged = onWishlistChanged,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Löschen-Button nur anzeigen, wenn onDelete nicht null ist
                        onDelete?.let { deleteFunction ->
                            IconButton(
                                onClick = { deleteFunction() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription =
                                        stringResource(R.string.favorite_delete),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
        }
}