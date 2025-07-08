package de.syntax_institut.androidabschlussprojekt.data.repositories

import androidx.paging.testing.*
import androidx.test.core.app.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.remote.*
import de.syntax_institut.androidabschlussprojekt.data.remote.dto.*
import de.syntax_institut.androidabschlussprojekt.data.remote.wrappers.*
import io.mockk.*
import junit.framework.TestCase.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*


@RunWith(AndroidJUnit4::class)
class GamePagingSourceTest {

    private lateinit var mockApi: RawgApi
    private lateinit var repository: GameRepository

    private val testGameDtos = listOf(
        GameDto(
            id = 1,
            slug = "test-game-1",
            name = "Test Game 1",
            released = "2023-01-01",
            backgroundImage = "https://example.com/image1.jpg",
            rating = 4.5f,
            description = "Test description 1",
            metacritic = 80,
            website = "https://example.com/game1",
            esrbRating = EsrbRatingDto(
                id = 1,
                name = "USK 12"
            ),
            genres = listOf(GenreDto(1, "Action")),
            platforms = listOf(PlatformWrapperDto(PlatformDto(1, "PC"))),
            developers = listOf(CompanyDto(1, "Dev Studio 1")),
            publishers = listOf(CompanyDto(2, "Publisher 1")),
            tags = listOf(TagDto(1, "Tag1")),
            shortScreenshots = listOf(ScreenshotDto(1, "https://example.com/screenshot1.jpg")),
            stores = listOf(StoreWrapperDto(StoreDto(1, "Steam"))),
            playtime = 12,
        ),
        GameDto(
            id = 2,
            slug = "test-game-2",
            name = "Test Game 2",
            released = "2023-01-02",
            backgroundImage = "https://example.com/image2.jpg",
            rating = 4.0f,
            description = "Test description 2",
            metacritic = 85,
            website = "https://example.com/game2",
            esrbRating = EsrbRatingDto(2, "USK 16"),
            genres = listOf(GenreDto(2, "Adventure")),
            platforms = listOf(PlatformWrapperDto(PlatformDto(2, "PlayStation 5"))),
            developers = listOf(CompanyDto(2, "Dev Studio 2")),
            publishers = listOf(CompanyDto(3, "Publisher 2")),
            tags = listOf(TagDto(2, "Tag2")),
            shortScreenshots = listOf(ScreenshotDto(2, "https://example.com/screenshot2.jpg")),
            stores = listOf(StoreWrapperDto(StoreDto(2, "Epic"))),
            playtime = 15,
        )
    )

    @Before
    fun setup() {
        mockApi = mockk(relaxed = true)
        repository = GameRepository(
            api = mockApi,
            gameCacheDao = mockk(relaxed = true),
            favoriteGameDao = mockk(relaxed = true),
            gameDetailCacheDao = mockk(relaxed = true),
            context = ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun pagingSourceLoadsFirstPageSuccessfully() = runTest {
        // Given
        val mockResponse = GamesResponse(
            results = testGameDtos.map { game ->
                GameDto(
                    id = game.id,
                    slug = game.slug,
                    name = game.name,
                    released = game.released,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    description = game.description,
                    metacritic = game.metacritic,
                    website = game.website,
                    esrbRating = game.esrbRating,
                    genres = game.genres,
                    platforms = game.platforms,
                    developers = game.developers,
                    publishers = game.publishers,
                    tags = game.tags,
                    shortScreenshots = game.shortScreenshots,
                    stores = game.stores,
                    playtime = game.playtime,
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
        assertEquals(2, snapshot.size)
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 2", snapshot[1].title)
    }

    @Test
    fun pagingSourceLoadsMultiplePages() = runTest {
        // Given
        val page1Games = testGameDtos.take(1)
        val page2Games = testGameDtos.drop(1)
        
        val page1Response = GamesResponse(
            results = page1Games.map { game ->
                GameDto(
                    id = game.id,
                    slug = game.slug,
                    name = game.name,
                    released = game.released,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    description = game.description,
                    metacritic = game.metacritic,
                    website = game.website,
                    esrbRating = game.esrbRating,
                    genres = game.genres,
                    platforms = game.platforms,
                    developers = game.developers,
                    publishers = game.publishers,
                    tags = game.tags,
                    shortScreenshots = game.shortScreenshots,
                    stores = game.stores,
                    playtime = game.playtime,
                )
            },
            next = "https://api.rawg.io/api/games?page=2",
            previous = null
        )

        val page2Response = GamesResponse(
            results = page2Games.map { game ->
                GameDto(
                    id = game.id,
                    slug = game.slug,
                    name = game.name,
                    released = game.released,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    description = game.description,
                    metacritic = game.metacritic,
                    website = game.website,
                    esrbRating = game.esrbRating,
                    genres = game.genres,
                    platforms = game.platforms,
                    developers = game.developers,
                    publishers = game.publishers,
                    tags = game.tags,
                    shortScreenshots = game.shortScreenshots,
                    stores = game.stores,
                    playtime = game.playtime,
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
            scrollTo(index = 1) // Trigger loading of second page
        }
        assertEquals(2, snapshot.size)
        assertEquals("Test Game 1", snapshot[0].title)
        assertEquals("Test Game 2", snapshot[1].title)
    }

    @Test
    fun pagingSourceAppliesRatingFilterCorrectly() = runTest {
        // Given
        val mockResponse = GamesResponse(
            results = testGameDtos.map { game ->
                GameDto(
                    id = game.id,
                    slug = game.slug,
                    name = game.name,
                    released = game.released,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    description = game.description,
                    metacritic = game.metacritic,
                    website = game.website,
                    esrbRating = game.esrbRating,
                    genres = game.genres,
                    platforms = game.platforms,
                    developers = game.developers,
                    publishers = game.publishers,
                    tags = game.tags,
                    shortScreenshots = game.shortScreenshots,
                    stores = game.stores,
                    playtime = game.playtime,
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
        assertEquals(1, snapshot.size) // Only games with rating >= 4.0
        assertEquals("Test Game 1", snapshot[0].title) // rating 4.5
    }

    @Test
    fun pagingSourceHandlesApiError() = runTest {
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
    fun pagingSourceHandlesNetworkException() = runTest {
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
    fun pagingSourceWithFiltersWorksCorrectly() = runTest {
        // Given
        val mockResponse = GamesResponse(
            results = testGameDtos.map { game ->
                GameDto(
                    id = game.id,
                    slug = game.slug,
                    name = game.name,
                    released = game.released,
                    backgroundImage = game.backgroundImage,
                    rating = game.rating,
                    description = game.description,
                    metacritic = game.metacritic,
                    website = game.website,
                    esrbRating = game.esrbRating,
                    genres = game.genres,
                    platforms = game.platforms,
                    developers = game.developers,
                    publishers = game.publishers,
                    tags = game.tags,
                    shortScreenshots = game.shortScreenshots,
                    stores = game.stores,
                    playtime = game.playtime,
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
        assertEquals(2, snapshot.size)
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
    fun pagingSourceHandlesEmptyResponse() = runTest {
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