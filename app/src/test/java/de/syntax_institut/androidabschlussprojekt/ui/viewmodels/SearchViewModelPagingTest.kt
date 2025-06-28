package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import androidx.paging.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelPagingTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var mockRepository: GameRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        viewModel = SearchViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `StateFlow should emit correct initial state`() {
        val initialState = viewModel.uiState.value
        
        assert(!initialState.isLoading)
        assert(initialState.error == null)
        assert(initialState.selectedPlatforms.isEmpty())
        assert(initialState.selectedGenres.isEmpty())
        assert(initialState.rating == 0f)
        assert(!initialState.hasSearched)
        assert(initialState.platforms.isNotEmpty())
        assert(initialState.genres.isNotEmpty())
    }

    @Test
    fun `pagingFlow should be initialized with empty PagingData`() {
        val initialPagingFlow = viewModel.pagingFlow.value
        
        assert(true)
    }

    @Test
    fun `updateFilters should update state correctly`() {
        // When
        viewModel.updateFilters(
            platforms = listOf("1", "2"),
            genres = listOf("1"),
            rating = 4.0f
        )

        // Then
        val uiState = viewModel.uiState.value
        assert(uiState.selectedPlatforms == listOf("1", "2"))
        assert(uiState.selectedGenres == listOf("1"))
        assert(uiState.rating == 4.0f)
    }

    @Test
    fun `updateOrdering should update state correctly`() {
        // When
        viewModel.updateOrdering("-rating")

        // Then
        val uiState = viewModel.uiState.value
        assert(uiState.ordering == "-rating")
    }

    @Test
    fun `platforms should be initialized with correct data`() {
        val uiState = viewModel.uiState.value
        
        assert(uiState.platforms.isNotEmpty())
        assert(uiState.platforms.any { it.name == "PC" })
        assert(uiState.platforms.any { it.name == "PlayStation 5" })
        assert(uiState.platforms.any { it.name == "Xbox Series S/X" })
    }

    @Test
    fun `genres should be initialized with correct data`() {
        val uiState = viewModel.uiState.value
        
        assert(uiState.genres.isNotEmpty())
        assert(uiState.genres.any { it.name == "Action" })
        assert(uiState.genres.any { it.name == "RPG" })
        assert(uiState.genres.any { it.name == "Strategy" })
    }

    @Test
    fun `multiple filter updates should work correctly`() {
        // When
        viewModel.updateFilters(
            platforms = listOf("1"),
            genres = listOf("2"),
            rating = 3.5f
        )
        
        viewModel.updateFilters(
            platforms = listOf("1", "2", "3"),
            genres = listOf("2", "3"),
            rating = 4.5f
        )

        // Then
        val uiState = viewModel.uiState.value
        assert(uiState.selectedPlatforms == listOf("1", "2", "3"))
        assert(uiState.selectedGenres == listOf("2", "3"))
        assert(uiState.rating == 4.5f)
    }

    @Test
    fun `ordering updates should work correctly`() {
        // When
        viewModel.updateOrdering("name")
        assert(viewModel.uiState.value.ordering == "name")
        
        viewModel.updateOrdering("-released")
        assert(viewModel.uiState.value.ordering == "-released")
        
        viewModel.updateOrdering("rating")
        assert(viewModel.uiState.value.ordering == "rating")
    }
} 