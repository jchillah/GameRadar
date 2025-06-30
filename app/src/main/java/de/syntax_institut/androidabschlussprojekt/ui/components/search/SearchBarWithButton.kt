package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*

@Composable
fun SearchBarWithButton(
    modifier: Modifier = Modifier,
    searchText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean = false,
    onClear: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchText,
            onValueChange = onTextChange,
            label = { Text("Suche nach Spielen") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                Row {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    if (searchText.text.isNotBlank() && onClear != null) {
                        IconButton(onClick = { onClear() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Leeren"
                            )
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSearchClick) {
            Text("Suchen")
        }
    }
}
