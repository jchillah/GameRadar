package de.syntax_institut.androidabschlussprojekt.ui.screens

//noinspection SuspiciousImport
import android.R
import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.core.net.*
import androidx.navigation.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.detail.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    gameId: Int,
    navController: NavHostController,
    vm: DetailViewModel = koinViewModel(),
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    val emptyString = ""

    LaunchedEffect(gameId) {
        vm.loadDetail(gameId)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(state.game?.title ?: emptyString) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painterResource(id = R.drawable.ic_media_previous),
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                FavoriteButton()
            }
        )
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
            }
            when {
                state.isLoading -> {
                    ShimmerPlaceholder()
                }
                state.error != null -> {
                    Text(
                        text = "Fehler: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    state.game?.let { game ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            GameHeaderImage(imageUrl = game.imageUrl.toString())
                            GameMetaInfo(
                                title = game.title,
                                releaseDate = game.releaseDate ?: emptyString,
                                rating = game.rating.toDouble()
                            )
                            GameDescription(description = game.description)
                            Spacer(modifier = Modifier.height(16.dp))

                            SectionCard("Plattformen") { ChipRow(game.platforms, emptyString) }
                            SectionCard("Genres") { ChipRow(game.genres, emptyString) }
                            SectionCard("Entwickler") { ChipFlowRow(game.developers, emptyString) }
                            SectionCard("Publisher") { ChipFlowRow(game.publishers, emptyString) }
                            SectionCard("USK/ESRB") { game.esrbRating?.let { ChipFlowRow(listOf(it), emptyString) } }
                            SectionCard("Tags") { ChipFlowRow(game.tags, emptyString) }
                            SectionCard("Stores") { ChipRow(game.stores, emptyString) }

                            SectionCard("Metacritic & Spielzeit") {
                                game.metacritic?.let {
                                    Text("Metacritic: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                game.playtime?.let {
                                    Text("Durchschnittliche Spielzeit: $it Std.", style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            SectionCard("Screenshots") { ScreenshotGallery(game.screenshots) }

                            game.website?.takeIf { it.isNotBlank() }?.let { url ->
                                SectionCard("Website") {
                                    TextButton(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Text("Website besuchen", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}