package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.*


@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteChanged: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    showAnimation: Boolean = true,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isFavorite && showAnimation) 360f else 0f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "rotation"
    )

    IconButton(
        onClick = {
            if (enabled) {
                isPressed = true
                onFavoriteChanged(!isFavorite)
                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    isPressed = false
                }
            }
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .semantics {
                // contentDescription wird im Icon gesetzt, nicht hier
            },
        enabled = enabled
    ) {
        AnimatedContent(
            targetState = isFavorite,
            transitionSpec = {
                (scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(200)
                ) + fadeIn(animationSpec = tween(200))).togetherWith(
                    scaleOut(
                        targetScale = 0.5f,
                        animationSpec = tween(200)
                    ) + fadeOut(animationSpec = tween(200))
                )
            }
        ) { favorite ->
            Icon(
                imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (favorite) stringResource(R.string.favorite_remove) else stringResource(
                    R.string.favorite_add
                ),
                tint = if (favorite) Color.Red else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonPreview() {
    FavoriteButton(isFavorite = false)
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonSelectedPreview() {
    FavoriteButton(isFavorite = true)
}