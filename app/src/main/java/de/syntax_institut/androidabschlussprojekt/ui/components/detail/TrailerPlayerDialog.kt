package de.syntax_institut.androidabschlussprojekt.ui.components.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.google.accompanist.systemuicontroller.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@Composable
fun TrailerPlayerDialog(
    viewModel: TrailerPlayerViewModel = koinViewModel(),
) {
    val systemUiController = rememberSystemUiController()
    var isSystemUiHidden by remember { mutableStateOf(false) }
    val showDialog by viewModel.showDialog.collectAsState()
    val movie = viewModel.currentMovie.collectAsState().value
    if (!showDialog || movie == null) return

    // Systemleisten je nach State ein-/ausblenden
    LaunchedEffect(isSystemUiHidden) {
        systemUiController.isSystemBarsVisible = !isSystemUiHidden
    }

    Dialog(onDismissRequest = { viewModel.closeDialog() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TrailerPlayerView(
                movie = movie,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isSystemUiHidden = !isSystemUiHidden }) {
                    Icon(
                        imageVector = if (isSystemUiHidden) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isSystemUiHidden) "Fullscreen verlassen" else "Fullscreen-Modus",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(onClick = { viewModel.closeDialog() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Schließen",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrailerPlayerDialogPreview() {
    TrailerPlayerDialog(
        viewModel = koinViewModel()
    )
}
