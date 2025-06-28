package de.syntax_institut.androidabschlussprojekt.ui.integration

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import androidx.paging.testing.scrollTo
import androidx.paging.testing.appendScrollWhile
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.data.repositories.GameRepository
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.SearchViewModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PagingDataTest : KoinTest {

    private lateinit var mockRepository: GameRepository
    private lateinit var viewModel: SearchViewModel

    private val testGames = (1..50).map { index ->
        Game(
            id = index,
            title = "Test Game $index",
            releaseDate = "2023-01-${String.format("%02d", index)}",
            imageUrl = "https://example.com/image$index.jpg",
            rating = (3.0f + (index % 3) * 0.5f),
            description = "Test description $index"
        )
    }

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
    fun `paging data loads initial items correctly`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot()
        assertEquals(20, snapshot.size)
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 20", snapshot[19].title)
    }

    @Test
    fun `paging data scrolls and loads more items`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot {
            // Scroll to the 30th item
            scrollTo(index = 30)
        }
        
        assertEquals(31, snapshot.size) // Should have loaded up to index 30
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 30", snapshot[30].title)
    }

    @Test
    fun `paging data scrolls until condition is met`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot {
            // Scroll until we find a game with rating >= 4.5
            appendScrollWhile { game -> game.rating < 4.5f }
        }
        
        assertTrue(snapshot.isNotEmpty())
        assertTrue(snapshot.any { it.rating >= 4.5f })
    }

    @Test
    fun `paging data with filters works correctly`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot()
        assertTrue(snapshot.all { it.rating >= 4.0f })
    }

    @Test
    fun `paging data with ordering works correctly`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot()
        assertTrue(snapshot.isNotEmpty())
        // Verify ordering (first few items should be in alphabetical order)
        for (i in 0 until minOf(5, snapshot.size - 1)) {
            assertTrue(snapshot[i].title <= snapshot[i + 1].title)
        }
    }

    @Test
    fun `paging data handles empty results`() = runTest {
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }

    @Test
    fun `paging data handles large datasets`() = runTest {
        // Given
        val largeGameList = (1..1000).map { index ->
            Game(
                id = index,
                title = "Large Test Game $index",
                releaseDate = "2023-01-${String.format("%02d", (index % 30) + 1)}",
                imageUrl = "https://example.com/large$index.jpg",
                rating = (1.0f + (index % 5) * 0.8f),
                description = "Large test description $index"
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
        val snapshot = viewModel.pagingFlow.value.asSnapshot {
            // Scroll to a middle point
            scrollTo(index = 500)
        }
        
        assertEquals(501, snapshot.size)
        assertEquals("Large Test Game 1", snapshot[0].title)
        assertEquals("Large Test Game 500", snapshot[500].title)
    }
} 