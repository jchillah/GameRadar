package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.*

@Composable
fun WishlistButton(
    modifier: Modifier = Modifier,
    isInWishlist: Boolean = false,
    onWishlistChanged: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    showAnimation: Boolean = true,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by
    animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    val rotation by
    animateFloatAsState(
        targetValue = if (isInWishlist && showAnimation) 360f else 0f,
        animationSpec = tween(500, easing = EaseOutBack),
        label = "rotation"
    )

    IconButton(
        onClick = {
            if (enabled) {
                isPressed = true
                onWishlistChanged(!isInWishlist)
                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    isPressed = false
                }
            }
        },
        modifier =
            modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            },
        enabled = enabled
    ) {
        AnimatedContent(
            targetState = isInWishlist,
            transitionSpec = {
                (scaleIn(initialScale = 0.5f, animationSpec = tween(200)) +
                        fadeIn(animationSpec = tween(200)))
                    .togetherWith(
                        scaleOut(targetScale = 0.5f, animationSpec = tween(200)) +
                                fadeOut(animationSpec = tween(200))
                    )
            }
        ) { inWishlist ->
            Icon(
                imageVector = if (inWishlist) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription =
                    if (inWishlist) stringResource(R.string.wishlist_marked)
                    else stringResource(R.string.wishlist_not_marked),
                tint =
                    if (inWishlist) Color(0xFFFFD700)
                    else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistButtonPreview() {
    WishlistButton(isInWishlist = false)
}

@Preview(showBackground = true)
@Composable
fun WishlistButtonSelectedPreview() {
    WishlistButton(isInWishlist = true)
}
