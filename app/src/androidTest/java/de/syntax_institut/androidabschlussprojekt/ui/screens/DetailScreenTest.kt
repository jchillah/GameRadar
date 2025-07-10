package de.syntax_institut.androidabschlussprojekt.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.*
import androidx.navigation.testing.*
import androidx.test.core.app.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.ui.states.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import io.mockk.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_showsLoadingState() {
        // Given
        val mockViewModel = mockk<DetailViewModel>()
        val loadingState = DetailUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(loadingState)

        // When
        composeTestRule.setContent {
            DetailScreen(
                gameId = 1,
                navController = TestNavHostController(ApplicationProvider.getApplicationContext()),
                vm = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithTag("loadingIndicator").assertExists()
    }

    @Test
    fun detailScreen_showsGameDetails() {
        // Given
        val mockViewModel = mockk<DetailViewModel>()
        val game = Game(
            id = 1,
            slug = "test-game",
            title = "Test Game",
            releaseDate = "2023-01-01",
            rating = 4.5f,
            imageUrl = "https://example.com/image.jpg",
            description = "Test description"
        )
        val gameState = DetailUiState(game = game)
        every { mockViewModel.uiState } returns MutableStateFlow(gameState)

        // When
        composeTestRule.setContent {
            DetailScreen(
                gameId = 1,
                navController = TestNavHostController(ApplicationProvider.getApplicationContext()),
                vm = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test Game").assertExists()
        composeTestRule.onNodeWithText("Release: 2023-01-01").assertExists()
        composeTestRule.onNodeWithText("Rating: 4.5").assertExists()
        composeTestRule.onNodeWithText("Test description").assertExists()
    }

    @Test
    fun detailScreen_showsErrorState() {
        // Given
        val mockViewModel = mockk<DetailViewModel>()
        val errorState = DetailUiState(error = "Network error")
        every { mockViewModel.uiState } returns MutableStateFlow(errorState)

        // When
        composeTestRule.setContent {
            DetailScreen(
                gameId = 1,
                navController = TestNavHostController(ApplicationProvider.getApplicationContext()),
                vm = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Fehler: Network error").assertExists()
    }

    @Test
    fun detailScreen_callsLoadDetailOnLaunch() {
        // Given
        val mockViewModel = mockk<DetailViewModel>(relaxed = true)
        val initialState = DetailUiState()
        every { mockViewModel.uiState } returns MutableStateFlow(initialState)

        // When
        composeTestRule.setContent {
            DetailScreen(
                gameId = 123,
                navController = TestNavHostController(ApplicationProvider.getApplicationContext()),
                vm = mockViewModel
            )
        }

        // Then
        verify { mockViewModel.loadDetail(123) }
    }
} 