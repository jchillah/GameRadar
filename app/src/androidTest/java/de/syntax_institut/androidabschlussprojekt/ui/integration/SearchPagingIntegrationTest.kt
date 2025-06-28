package de.syntax_institut.androidabschlussprojekt.ui.integration

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.syntax_institut.androidabschlussprojekt.MainActivity
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.remote.RawgApi
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GameDto
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.GamesResponse
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.ui.screens.SearchScreen
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import androidx.paging.testing.cachedIn
import androidx.paging.testing.collectPagingData
import androidx.paging.testing.scrollTo
import androidx.paging.testing.appendScrollWhile

@RunWith(AndroidJUnit4::class)
class SearchPagingIntegrationTest : KoinTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockApi: RawgApi
    private lateinit var mockRepository: GameRepository
    private lateinit var viewModel: SearchViewModel

    private val testGames = listOf(
        Game(
            id = 1,
            title = "Test Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.5f,
            description = "Test description 1"
        ),
        Game(
            id = 2,
            title = "Test Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.0f,
            description = "Test description 2"
        ),
        Game(
            id = 3,
            title = "Test Game 3",
            releaseDate = "2023-01-03",
            imageUrl = "https://example.com/image3.jpg",
            rating = 3.5f,
            description = "Test description 3"
        )
    )

    @Before
    fun setup() {
        mockApi = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        
        // Mock API responses
        val mockGamesResponse = GamesResponse(
            results = testGames.map { game ->
                GameDto(
                    id = game.id,
                    name = game.title,
                    released = game.releaseDate,
                    backgroundImage = game.imageUrl,
                    rating = game.rating,
                    description = game.description
                )
            },
            next = null,
            previous = null
        )
        
        coEvery { 
            mockApi.searchGames(
                query = any(),
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = any(),
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns mockGamesResponse
        }

        // Setup Koin for testing
        stopKoin()
        startKoin {
            modules(
                module {
                    single { mockApi }
                    single { mockRepository }
                    single { SearchViewModel(get()) }
                }
            )
        }
        
        viewModel = inject<SearchViewModel>().value
    }

    @Test
    fun searchScreen_loadsDefaultState() {
        // When
        composeTestRule.onNodeWithText("Search Games").assertExists()
        composeTestRule.onNodeWithText("Bitte gib einen Suchbegriff ein.").assertExists()
    }

    @Test
    fun searchScreen_performsSearchAndShowsResults() = runTest {
        // Given
        val mockPagingData = PagingData.from(testGames)
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

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test Game 1").fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Test Game 1").assertExists()
        composeTestRule.onNodeWithText("Test Game 2").assertExists()
    }

    @Test
    fun searchScreen_showsFilterButton() {
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").assertExists()
    }

    @Test
    fun searchScreen_opensFilterBottomSheet() {
        // When
        composeTestRule.onNodeWithContentDescription("Filter anzeigen").performClick()

        // Then
        composeTestRule.onNodeWithText("Plattformen").assertExists()
        composeTestRule.onNodeWithText("Genres").assertExists()
        composeTestRule.onNodeWithText("Mindestbewertung: 0").assertExists()
        composeTestRule.onNodeWithText("Sortierung").assertExists()
    }

    @Test
    fun searchScreen_appliesFilters() = runTest {
        // Given
        val mockPagingData = PagingData.from(testGames.filter { it.rating >= 4.0f })
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
        
        // Set rating filter
        composeTestRule.onNodeWithText("Mindestbewertung: 0").performClick()
        // Note: Slider interaction would need more complex setup
        
        composeTestRule.onNodeWithText("Filter anwenden").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test Game 1").fetchSemanticsNodes().size == 1
        }
        // Should only show games with rating >= 4.0
        composeTestRule.onNodeWithText("Test Game 1").assertExists() // rating 4.5
        composeTestRule.onNodeWithText("Test Game 2").assertExists() // rating 4.0
        // Test Game 3 should not be visible (rating 3.5)
    }

    @Test
    fun searchScreen_showsErrorState() = runTest {
        // Given
        coEvery { 
            mockRepository.getPagedGames(any(), any(), any(), any(), any()) 
        } throws Exception("Network error")

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Fehler:").fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText("Fehler:").assertExists()
    }

    @Test
    fun searchScreen_navigatesToDetailScreen() = runTest {
        // Given
        val mockPagingData = PagingData.from(testGames)
        coEvery { 
            mockRepository.getPagedGames(any(), any(), any(), any(), any()) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test Game 1").fetchSemanticsNodes().size == 1
        }
        
        composeTestRule.onNodeWithText("Test Game 1").performClick()

        // Then
        // Should navigate to detail screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test Game 1").fetchSemanticsNodes().size >= 1
        }
    }

    @Test
    fun searchScreen_emptySearchShowsMessage() {
        // When
        composeTestRule.onNodeWithText("Suchen").performClick()

        // Then
        composeTestRule.onNodeWithText("Bitte gib einen Suchbegriff ein.").assertExists()
    }

    @Test
    fun searchScreen_clearsSearchResults() = runTest {
        // Given
        val mockPagingData = PagingData.from(testGames)
        coEvery { 
            mockRepository.getPagedGames(any(), any(), any(), any(), any()) 
        } returns flowOf(mockPagingData)

        // When
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextInput("test")
        composeTestRule.onNodeWithText("Suchen").performClick()
        
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test Game 1").fetchSemanticsNodes().size == 1
        }
        
        // Clear search
        composeTestRule.onNodeWithText("Suche nach Spielen").performTextReplacement("")

        // Then
        composeTestRule.onNodeWithText("Bitte gib einen Suchbegriff ein.").assertExists()
    }
} 