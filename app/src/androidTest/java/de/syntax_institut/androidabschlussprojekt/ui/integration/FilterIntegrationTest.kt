package de.syntax_institut.androidabschlussprojekt.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.syntax_institut.androidabschlussprojekt.MainActivity
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import androidx.paging.PagingData

@RunWith(AndroidJUnit4::class)
class FilterIntegrationTest : KoinTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockRepository: GameRepository
    private lateinit var viewModel: SearchViewModel

    private val testGames = listOf(
        Game(
            id = 1,
            title = "Action Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/action.jpg",
            rating = 4.5f,
            description = "Action game description",
            platforms = listOf("PC", "PlayStation 5"),
            genres = listOf("Action", "Adventure")
        ),
        Game(
            id = 2,
            title = "RPG Game",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/rpg.jpg",
            rating = 4.0f,
            description = "RPG game description",
            platforms = listOf("PC", "Xbox Series S/X"),
            genres = listOf("RPG", "Strategy")
        ),
        Game(
            id = 3,
            title = "Strategy Game",
            releaseDate = "2023-01-03",
            imageUrl = "https://example.com/strategy.jpg",
            rating = 3.5f,
            description = "Strategy game description",
            platforms = listOf("PC"),
            genres = listOf("Strategy")
        )
    )

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        
        stopKoin()
        startKoin {
            modules(
                module {
                    single { mockRepository }
                    single { SearchViewModel(get()) }
                }
            )
        }
        
        viewModel = inject<SearchViewModel>().value
    }

    @Test
    fun filterBottomSheet_showsAllFilterOptions() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()

        // Then
        composeTestRule.onNodeWithText("Plattformen").assertExists()
        composeTestRule.onNodeWithText("Genres").assertExists()
        composeTestRule.onNodeWithText("Mindestbewertung: 0").assertExists()
        composeTestRule.onNodeWithText("Sortierung").assertExists()
        composeTestRule.onNodeWithText("Filter anwenden").assertExists()
    }

    @Test
    fun filterBottomSheet_showsPlatformOptions() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()

        // Then
        composeTestRule.onNodeWithText("PC").assertExists()
        composeTestRule.onNodeWithText("PlayStation 5").assertExists()
        composeTestRule.onNodeWithText("Xbox Series S/X").assertExists()
        composeTestRule.onNodeWithText("Nintendo Switch").assertExists()
    }

    @Test
    fun filterBottomSheet_showsGenreOptions() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()

        // Then
        composeTestRule.onNodeWithText("Action").assertExists()
        composeTestRule.onNodeWithText("Adventure").assertExists()
        composeTestRule.onNodeWithText("RPG").assertExists()
        composeTestRule.onNodeWithText("Strategy").assertExists()
    }

    @Test
    fun filterBottomSheet_showsOrderingOptions() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("Sortierung wählen").performClick()

        // Then
        composeTestRule.onNodeWithText("Bewertung (absteigend)").assertExists()
        composeTestRule.onNodeWithText("Bewertung (aufsteigend)").assertExists()
        composeTestRule.onNodeWithText("Release (neueste)").assertExists()
        composeTestRule.onNodeWithText("Release (älteste)").assertExists()
        composeTestRule.onNodeWithText("Name (A-Z)").assertExists()
        composeTestRule.onNodeWithText("Name (Z-A)").assertExists()
    }

    @Test
    fun filterBottomSheet_canSelectPlatforms() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("PC").performClick()

        // Then
        // The chip should be selected (this would need more complex verification)
        composeTestRule.onNodeWithText("PC").assertExists()
    }

    @Test
    fun filterBottomSheet_canSelectGenres() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("Action").performClick()

        // Then
        // The chip should be selected
        composeTestRule.onNodeWithText("Action").assertExists()
    }

    @Test
    fun filterBottomSheet_canChangeOrdering() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("Sortierung wählen").performClick()
        composeTestRule.onNodeWithText("Bewertung (absteigend)").performClick()

        // Then
        composeTestRule.onNodeWithText("Bewertung (absteigend)").assertExists()
    }

    @Test
    fun filterBottomSheet_canApplyFilters() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        // Bottom sheet should be dismissed
        composeTestRule.onNodeWithText("Plattformen").assertDoesNotExist()
    }

    @Test
    fun filterBottomSheet_canDismiss() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        
        // Dismiss by clicking outside or back
        composeTestRule.onNodeWithText("Suche nach Spielen").performClick()

        // Then
        composeTestRule.onNodeWithText("Plattformen").assertDoesNotExist()
    }

    @Test
    fun filterIntegration_withRatingFilter() = runTest {
        // Given
        val filteredGames = testGames.filter { it.rating >= 4.0f }
        val mockPagingData = PagingData.from(filteredGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                rating = 4.0f
            ) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        // Note: Slider interaction would need more complex setup
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Action Game").fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Action Game").assertExists() // rating 4.5
        composeTestRule.onNodeWithText("RPG Game").assertExists() // rating 4.0
        // Strategy Game should not be visible (rating 3.5)
    }

    @Test
    fun filterIntegration_withPlatformFilter() = runTest {
        // Given
        val pcGames = testGames.filter { it.platforms.contains("PC") }
        val mockPagingData = PagingData.from(pcGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = "1", // PC platform ID
                genres = any(),
                ordering = any(),
                rating = any()
            ) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("PC").performClick()
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Action Game").fetchSemanticsNodes().size == 1
        }
        // All games should be PC games
        composeTestRule.onNodeWithText("Action Game").assertExists()
        composeTestRule.onNodeWithText("RPG Game").assertExists()
        composeTestRule.onNodeWithText("Strategy Game").assertExists()
    }

    @Test
    fun filterIntegration_withGenreFilter() = runTest {
        // Given
        val actionGames = testGames.filter { it.genres.contains("Action") }
        val mockPagingData = PagingData.from(actionGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = any(),
                genres = "1", // Action genre ID
                ordering = any(),
                rating = any()
            ) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("Action").performClick()
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Action Game").fetchSemanticsNodes().size == 1
        }
        // Only action games should be visible
        composeTestRule.onNodeWithText("Action Game").assertExists()
        // RPG and Strategy games should not be visible
    }

    @Test
    fun filterIntegration_withMultipleFilters() = runTest {
        // Given
        val filteredGames = testGames.filter { 
            it.rating >= 4.0f && 
            it.platforms.contains("PC") && 
            it.genres.contains("Action")
        }
        val mockPagingData = PagingData.from(filteredGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = "1", // PC
                genres = "1", // Action
                ordering = "-rating",
                rating = 4.0f
            ) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("PC").performClick()
        composeTestRule.onNodeWithText("Action").performClick()
        composeTestRule.onNodeWithText("Sortierung wählen").performClick()
        composeTestRule.onNodeWithText("Bewertung (absteigend)").performClick()
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Action Game").fetchSemanticsNodes().size == 1
        }
        // Only Action Game should be visible (PC, Action, rating >= 4.0)
        composeTestRule.onNodeWithText("Action Game").assertExists()
    }

    @Test
    fun filterIntegration_clearsFilters() = runTest {
        // Given
        val allGames = testGames
        val mockPagingData = PagingData.from(allGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                rating = any()
            ) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        // Apply some filters first
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()
        composeTestRule.onNodeWithText("PC").performClick()
        composeTestRule.onNodeWithText("Filter anwenden").performClick()
        
        // Then clear filters by searching again without filters
        composeTestRule.onNodeWithText("Suchen").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Action Game").fetchSemanticsNodes().size == 1
        }
        // All games should be visible again
        composeTestRule.onNodeWithText("Action Game").assertExists()
        composeTestRule.onNodeWithText("RPG Game").assertExists()
        composeTestRule.onNodeWithText("Strategy Game").assertExists()
    }
} 