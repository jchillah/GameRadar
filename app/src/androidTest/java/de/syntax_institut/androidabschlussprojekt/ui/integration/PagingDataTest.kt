package de.syntax_institut.androidabschlussprojekt.ui.integration

import androidx.paging.*
import androidx.paging.testing.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.di.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Test
import org.junit.runner.*
import org.koin.core.context.*
import org.koin.dsl.*
import org.koin.test.*
import kotlin.test.*

@RunWith(AndroidJUnit4::class)
class PagingDataTest : KoinTest {

    private lateinit var mockRepository: GameRepository
    private lateinit var viewModel: SearchViewModel

    private val testGames = (1..50).map { index ->
        Game(
            id = index,
            slug = "test-game-$index",
            title = "Test Game $index",
            releaseDate = "2023-01-${String.format("%02d", index)}",
            imageUrl = "https://example.com/image$index.jpg",
            rating = (3.0f + (index % 3) * 0.5f),
            description = "Test description $index",
            metacritic = 80 + (index % 20),
            website = "https://example.com/game$index",
            esrbRating = "USK 12",
            genres = listOf("Action", "Adventure"),
            platforms = listOf("PC", "PlayStation 5"),
            developers = listOf("Dev Studio $index"),
            publishers = listOf("Publisher $index"),
            tags = listOf("Tag1", "Tag2"),
            screenshots = listOf("https://example.com/screenshot$index.jpg"),
            stores = listOf("Steam", "Epic"),
            playtime = 10 + index,
            movies = emptyList()
        )
    }

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        
        stopKoin()
        startKoin {
            modules(
                networkModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                module {
                    single<GameRepository> { mockRepository }
                }
            )
        }
        
        viewModel = inject<SearchViewModel>().value
    }

    @Test
    fun pagingDataLoadsInitialItemsCorrectly() = runTest {
        // Given
        val initialGames = testGames.take(20)
        val mockPagingData = PagingData.from(initialGames)
        
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
        viewModel.search("test")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot()
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 20", snapshot[19].title)
    }

    @Test
    fun pagingDataScrollsAndLoadsMoreItems() = runTest {
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
        viewModel.search("test")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot {
            // Scroll to the 30th item
            scrollTo(index = 30)
        }
        
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 30", snapshot[30].title)
    }

    @Test
    fun pagingDataScrollsUntilConditionIsMet() = runTest {
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
        viewModel.search("test")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot {
            // Scroll until we find a game with rating >= 4.5
            appendScrollWhile { game -> game.rating < 4.5f }
        }
        
        assertTrue(snapshot.isNotEmpty())
        assertTrue(snapshot.any { it.rating >= 4.5f })
    }

    @Test
    fun pagingDataWithFiltersWorksCorrectly() = runTest {
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
        viewModel.updateFilters(
            platforms = listOf("1"),
            genres = listOf("1"),
            rating = 4.0f
        )
        viewModel.search("test")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot()
        assertTrue(snapshot.all { it.rating >= 4.0f })
    }

    @Test
    fun pagingDataWithOrderingWorksCorrectly() = runTest {
        // Given
        val sortedGames = testGames.sortedBy { it.title }
        val mockPagingData = PagingData.from(sortedGames)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = "name",
                rating = any()
            ) 
        } returns flowOf(mockPagingData)

        // When
        viewModel.updateOrdering("name")
        viewModel.search("test")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot()
        assertTrue(snapshot.isNotEmpty())
        // Verify ordering (first few items should be in alphabetical order)
        for (i in 0 until minOf(5, snapshot.size - 1)) {
            assertTrue(snapshot[i].title <= snapshot[i + 1].title)
        }
    }

    @Test
    fun pagingDataHandlesEmptyResults() = runTest {
        // Given
        val emptyPagingData = PagingData.empty<Game>()
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "nonexistent",
                platforms = any(),
                genres = any(),
                ordering = any(),
                rating = any()
            ) 
        } returns flowOf(emptyPagingData)

        // When
        viewModel.search("nonexistent")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }

    @Test
    fun pagingDataHandlesLargeDatasets() = runTest {
        // Given
        val largeGameList = (1..500).map { index ->
            Game(
                id = index,
                slug = "large-test-game-$index",
                title = "Large Test Game $index",
                releaseDate = "2023-01-${String.format("%02d", (index % 30) + 1)}",
                imageUrl = "https://example.com/large$index.jpg",
                rating = (1.0f + (index % 5) * 0.8f),
                description = "Large test description $index",
                metacritic = 80 + (index % 20),
                website = "https://example.com/large$index",
                esrbRating = "USK 12",
                genres = listOf("Action", "Adventure"),
                platforms = listOf("PC", "PlayStation 5"),
                developers = listOf("Dev $index"),
                publishers = listOf("Publisher $index"),
                tags = listOf("Indie", "Multiplayer"),
                screenshots = listOf("https://example.com/large${index}_1.jpg"),
                stores = listOf("Steam", "Epic"),
                playtime = 10 + index,
                movies = emptyList()
            )
        }
        val mockPagingData = PagingData.from(largeGameList)
        
        coEvery { 
            mockRepository.getPagedGames(
                query = "large",
                platforms = any(),
                genres = any(),
                ordering = any(),
                rating = any()
            ) 
        } returns flowOf(mockPagingData)

        // When
        viewModel.search("large")
        
        // Then
        val snapshot: List<Game> = viewModel.pagingFlow.asSnapshot {
            // Scroll to a middle point
            scrollTo(index = 499)
        }
        
        assertEquals("Large Test Game 1", snapshot[0].title)
        assertEquals("Large Test Game 500", snapshot[499].title)
    }
} 