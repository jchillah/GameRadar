package de.syntax_institut.androidabschlussprojekt.data.repositories

import androidx.paging.testing.*
import androidx.test.core.app.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import io.mockk.*
import junit.framework.TestCase.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*


@RunWith(AndroidJUnit4::class)
class GamePagingSourceTest {

    private lateinit var mockApi: RawgApi
    private lateinit var repository: GameRepository

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
        repository = GameRepository(
            mockApi,
            gameCacheDao = mockk(relaxed = true),
            context = ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun `paging source loads first page successfully`() = runTest {
        // Given
        val mockResponse = GamesResponse(
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
            next = "https://api.rawg.io/api/games?page=2",
            previous = null
        )

        coEvery { 
            mockApi.searchGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns mockResponse
        }

        // When
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = null,
            genres = null,
            ordering = null,
            rating = null
        )

        // Then
        val snapshot = pagingData.asSnapshot()
        assertEquals(3, snapshot.size)
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 2", snapshot[1].title)
        assertEquals("Test Game 3", snapshot[2].title)
    }

    @Test
    fun `paging source loads multiple pages`() = runTest {
        // Given
        val page1Games = testGames.take(2)
        val page2Games = testGames.drop(2)
        
        val page1Response = GamesResponse(
            results = page1Games.map { game ->
                GameDto(
                    id = game.id,
                    name = game.title,
                    released = game.releaseDate,
                    backgroundImage = game.imageUrl,
                    rating = game.rating,
                    description = game.description
                )
            },
            next = "https://api.rawg.io/api/games?page=2",
            previous = null
        )

        val page2Response = GamesResponse(
            results = page2Games.map { game ->
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
            previous = "https://api.rawg.io/api/games?page=1"
        )

        coEvery { 
            mockApi.searchGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns page1Response
        }

        coEvery { 
            mockApi.searchGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 2,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns page2Response
        }

        // When
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = null,
            genres = null,
            ordering = null,
            rating = null
        )

        // Then
        val snapshot = pagingData.asSnapshot {
            scrollTo(index = 2) // Trigger loading of second page
        }
        assertEquals(3, snapshot.size)
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 2", snapshot[1].title)
        assertEquals("Test Game 3", snapshot[2].title)
    }

    @Test
    fun `paging source applies rating filter correctly`() = runTest {
        // Given
        val mockResponse = GamesResponse(
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
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns mockResponse
        }

        // When
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = null,
            genres = null,
            ordering = null,
            rating = 4.0f
        )

        // Then
        val snapshot = pagingData.asSnapshot()
        assertTrue(snapshot.all { it.rating >= 4.0f })
        assertEquals(2, snapshot.size) // Only games with rating >= 4.0
        assertEquals("Test Game 1", snapshot[0].title) // rating 4.5
        assertEquals("Test Game 2", snapshot[1].title) // rating 4.0
    }

    @Test
    fun `paging source handles API error`() = runTest {
        // Given
        coEvery { 
            mockApi.searchGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns false
            every { code() } returns 500
        }

        // When & Then
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = null,
            genres = null,
            ordering = null,
            rating = null
        )

        // Should handle error gracefully
        val snapshot = pagingData.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }

    @Test
    fun `paging source handles network exception`() = runTest {
        // Given
        coEvery { 
            mockApi.searchGames(
                query = "test",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } throws Exception("Network error")

        // When & Then
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = null,
            genres = null,
            ordering = null,
            rating = null
        )

        // Should handle exception gracefully
        val snapshot = pagingData.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }

    @Test
    fun `paging source with filters works correctly`() = runTest {
        // Given
        val mockResponse = GamesResponse(
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
                query = "test",
                platforms = "1,2",
                genres = "1",
                ordering = "-rating",
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns mockResponse
        }

        // When
        val pagingData = repository.getPagedGames(
            query = "test",
            platforms = "1,2",
            genres = "1",
            ordering = "-rating",
            rating = null
        )

        // Then
        val snapshot = pagingData.asSnapshot()
        assertEquals(3, snapshot.size)
        // Verify that the API was called with correct parameters
        coVerify { 
            mockApi.searchGames(
                query = "test",
                platforms = "1,2",
                genres = "1",
                ordering = "-rating",
                page = 1,
                pageSize = any()
            ) 
        }
    }

    @Test
    fun `paging source handles empty response`() = runTest {
        // Given
        val emptyResponse = GamesResponse(
            results = emptyList(),
            next = null,
            previous = null
        )

        coEvery { 
            mockApi.searchGames(
                query = "nonexistent",
                platforms = any(),
                genres = any(),
                ordering = any(),
                page = 1,
                pageSize = any()
            ) 
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns emptyResponse
        }

        // When
        val pagingData = repository.getPagedGames(
            query = "nonexistent",
            platforms = null,
            genres = null,
            ordering = null,
            rating = null
        )

        // Then
        val snapshot = pagingData.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }
} 