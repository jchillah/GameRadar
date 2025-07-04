package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.ui.components.search.*

@Composable
fun PerformanceOptimizedLazyColumn(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
fun PerformanceOptimizedLazyColumnWithPlaceholders(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    isLoading: Boolean = false,
    placeholderCount: Int = 5,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        if (isLoading) {
            items(placeholderCount) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            content()
                }
    }
}

@Preview(showBackground = true)
@Composable
fun PerformanceOptimizedLazyColumnPreview() {
    PerformanceOptimizedLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(10) { index ->
            Text(
                text = "Item $index",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerformanceOptimizedLazyColumnWithPlaceholdersPreview() {
    PerformanceOptimizedLazyColumnWithPlaceholders(
        modifier = Modifier.fillMaxSize(),
        isLoading = true,
        placeholderCount = 5
    ) {
        items(10) { index ->
            Text(
                text = "Item $index",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerformanceOptimizedLazyColumnWithErrorPreview() {
    PerformanceOptimizedLazyColumnWithError(
        modifier = Modifier.fillMaxSize(),
        error = "Ein Fehler ist aufgetreten",
    ) {
        items(10) { index ->
            Text(
                text = "Item $index",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun PerformanceOptimizedLazyColumnWithError(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    isLoading: Boolean = false,
    error: String? = null,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        if (isLoading) {
            items(3) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else if (error != null) {
            item {
                ErrorCard(
                    error = error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else {
            content()
        }
    }
} 