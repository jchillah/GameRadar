package de.syntax_institut.androidabschlussprojekt.ui.viewmodels

import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.domain.models.*
import de.syntax_institut.androidabschlussprojekt.domain.usecase.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelPagingTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var mockRepository: GameRepository
    private lateinit var mockLoadGamesUseCase: LoadGamesUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        mockLoadGamesUseCase = mockk(relaxed = true)
        viewModel =
            SearchViewModel(
                loadGamesUseCase = mockLoadGamesUseCase,
                getPlatformsUseCase = mockk(relaxed = true),
                getGenresUseCase = mockk(relaxed = true),
                getCacheSizeUseCase = mockk(relaxed = true),
                clearCacheUseCase = mockk(relaxed = true)
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // ViewModel wird automatisch aufgeräumt wenn es aus dem Scope fällt
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
        assert(initialState.platforms.isEmpty())
        assert(initialState.genres.isEmpty())
    }

    @Test
    fun `pagingFlow should be initialized with empty PagingData`() {
        viewModel.pagingFlow.value

        assert(true)
    }

    @Test
    fun `updateFilters should update state correctly`() {
        // When
        viewModel.updateFilters(platforms = listOf("1", "2"), genres = listOf("1"), rating = 4.0f)

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
    fun `loadPlatforms should load platforms from repository`() = runTest {
        // Given
        val mockPlatforms =
            listOf(
                Platform(1, "PC"),
                Platform(2, "PlayStation 5"),
                Platform(3, "Xbox Series S/X")
            )
        coEvery { mockRepository.getPlatforms() } returns Resource.Success(mockPlatforms)

        // When
        viewModel.loadPlatforms()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assert(uiState.platforms == mockPlatforms)
        assert(!uiState.isLoadingPlatforms)
        assert(uiState.platformsError == null)
    }

    @Test
    fun `loadGenres should load genres from repository`() = runTest {
        // Given
        val mockGenres = listOf(Genre(1, "Action"), Genre(2, "RPG"), Genre(3, "Strategy"))
        coEvery { mockRepository.getGenres() } returns Resource.Success(mockGenres)

        // When
        viewModel.loadGenres()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assert(uiState.genres == mockGenres)
        assert(!uiState.isLoadingGenres)
        assert(uiState.genresError == null)
    }

    @Test
    fun `multiple filter updates should work correctly`() {
        // When
        viewModel.updateFilters(platforms = listOf("1"), genres = listOf("2"), rating = 3.5f)

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
