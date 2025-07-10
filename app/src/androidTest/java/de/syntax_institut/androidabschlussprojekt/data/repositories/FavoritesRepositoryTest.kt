package de.syntax_institut.androidabschlussprojekt.data.repositories

import androidx.room.*
import androidx.test.core.app.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.local.*
import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class FavoritesRepositoryTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: FavoriteGameDao
    private lateinit var repository: FavoritesRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GameDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteGameDao()
        repository = FavoritesRepository(
            favoriteGameDao = dao,
            repo = GameRepository(
                api = mockk(),
                gameCacheDao = mockk(),
                context = ApplicationProvider.getApplicationContext(),
                favoriteGameDao = mockk(),
                gameDetailCacheDao = mockk()
            )
        )
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun addAndGetFavorite() = runTest {
        // Given
        val game = Game(
            id = 1,
            slug = "test-game",
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = 85,
            website = "https://example.com",
            esrbRating = "T",
            genres = listOf("Action", "Adventure"),
            platforms = listOf("PC", "PlayStation 5"),
            developers = listOf("Test Developer"),
            publishers = listOf("Test Publisher"),
            tags = listOf("Action", "RPG"),
            screenshots = listOf("https://example.com/screenshot1.jpg"),
            stores = listOf("Steam", "PlayStation Store"),
            playtime = 20,
            movies = emptyList()
        )

        // When
        val addResult = repository.addFavorite(game)
        val retrieved = repository.getFavoriteById(1)

        // Then
        assertTrue(addResult is Resource.Success)
        assertNotNull(retrieved)
        assertEquals("Test Game", retrieved?.title)
        assertEquals(4.5f, retrieved?.rating ?: 0f, 0.01f)
        assertEquals(listOf("Action", "Adventure"), retrieved?.genres)
    }

    @Test
    fun getAllFavorites() = runTest {
        // Given
        val game1 = Game(
            id = 1,
            slug = "game-1",
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = 80,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev 1"),
            publishers = listOf("Pub 1"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        val game2 = Game(
            id = 2,
            slug = "game-2",
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = 85,
            website = null,
            esrbRating = null,
            genres = listOf("RPG"),
            platforms = listOf("PC"),
            developers = listOf("Dev 2"),
            publishers = listOf("Pub 2"),
            tags = listOf("RPG"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        repository.addFavorite(game1)
        repository.addFavorite(game2)
        val allFavorites = repository.getAllFavorites().first()

        // Then
        assertEquals(2, allFavorites.size)
        assertEquals("Game 2", allFavorites[0].title) // Neueste zuerst
        assertEquals("Game 1", allFavorites[1].title)
    }

    @Test
    fun isFavorite() = runTest {
        // Given
        val game = Game(
            id = 1,
            slug = "test-game",
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev"),
            publishers = listOf("Pub"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        val isFavoriteBefore = repository.isFavorite(1)
        repository.addFavorite(game)
        val isFavoriteAfter = repository.isFavorite(1)

        // Then
        assertFalse(isFavoriteBefore)
        assertTrue(isFavoriteAfter)
    }

    @Test
    fun removeFavorite() = runTest {
        // Given
        val game = Game(
            id = 1,
            slug = "test-game",
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev"),
            publishers = listOf("Pub"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        repository.addFavorite(game)
        val isFavoriteBefore = repository.isFavorite(1)
        val removeResult = repository.removeFavorite(1)
        val isFavoriteAfter = repository.isFavorite(1)

        // Then
        assertTrue(isFavoriteBefore)
        assertTrue(removeResult is Resource.Success)
        assertFalse(isFavoriteAfter)
    }

    @Test
    fun toggleFavorite() = runTest {
        // Given
        val game = Game(
            id = 1,
            slug = "test-game",
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev"),
            publishers = listOf("Pub"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        val toggleResult1 = repository.toggleFavorite(game)
        val isFavoriteAfter1 = repository.isFavorite(1)
        val toggleResult2 = repository.toggleFavorite(game)
        val isFavoriteAfter2 = repository.isFavorite(1)

        // Then
        assertTrue(toggleResult1 is Resource.Success)
        assertEquals(true, (toggleResult1 as Resource.Success).data)
        assertTrue(isFavoriteAfter1)
        
        assertTrue(toggleResult2 is Resource.Success)
        assertEquals(false, (toggleResult2 as Resource.Success).data)
        assertFalse(isFavoriteAfter2)
    }

    @Test
    fun clearAllFavorites() = runTest {
        // Given
        val game1 = Game(
            id = 1,
            slug = "game-1",
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev 1"),
            publishers = listOf("Pub 1"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        val game2 = Game(
            id = 2,
            slug = "game-2",
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("RPG"),
            platforms = listOf("PC"),
            developers = listOf("Dev 2"),
            publishers = listOf("Pub 2"),
            tags = listOf("RPG"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        repository.addFavorite(game1)
        repository.addFavorite(game2)
        val countBefore = repository.getFavoriteCount()
        val clearResult = repository.clearAllFavorites()
        val countAfter = repository.getFavoriteCount()

        // Then
        assertEquals(2, countBefore)
        assertTrue(clearResult is Resource.Success)
        assertEquals(0, countAfter)
    }

    @Test
    fun getFavoriteCount() = runTest {
        // Given
        val game1 = Game(
            id = 1,
            slug = "game-1",
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev 1"),
            publishers = listOf("Pub 1"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        val game2 = Game(
            id = 2,
            slug = "game-2",
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("RPG"),
            platforms = listOf("PC"),
            developers = listOf("Dev 2"),
            publishers = listOf("Pub 2"),
            tags = listOf("RPG"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        val countBefore = repository.getFavoriteCount()
        repository.addFavorite(game1)
        val countAfter1 = repository.getFavoriteCount()
        repository.addFavorite(game2)
        val countAfter2 = repository.getFavoriteCount()

        // Then
        assertEquals(0, countBefore)
        assertEquals(1, countAfter1)
        assertEquals(2, countAfter2)
    }

    @Test
    fun searchFavorites() = runTest {
        // Given
        val game1 = Game(
            id = 1,
            slug = "action-game",
            title = "Action Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Action game description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("Action"),
            platforms = listOf("PC"),
            developers = listOf("Dev 1"),
            publishers = listOf("Pub 1"),
            tags = listOf("Action"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        val game2 = Game(
            id = 2,
            slug = "rpg-game",
            title = "RPG Game",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "RPG game description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = listOf("RPG"),
            platforms = listOf("PC"),
            developers = listOf("Dev 2"),
            publishers = listOf("Pub 2"),
            tags = listOf("RPG"),
            screenshots = emptyList(),
            stores = listOf("Steam"),
            playtime = null,
            movies = emptyList()
        )

        // When
        repository.addFavorite(game1)
        repository.addFavorite(game2)
        
        val actionResults = repository.searchFavorites("Action").first()
        val rpgResults = repository.searchFavorites("RPG").first()
        val nonExistentResults = repository.searchFavorites("NonExistent").first()

        // Then
        assertEquals(1, actionResults.size)
        assertEquals("Action Game", actionResults[0].title)
        
        assertEquals(1, rpgResults.size)
        assertEquals("RPG Game", rpgResults[0].title)
        
        assertEquals(0, nonExistentResults.size)
    }

    @Test
    fun handleErrorWhenAddingFavorite() = runTest {
        // Given
        val invalidGame = Game(
            id = -1, // Ungültige ID könnte zu Fehler führen
            slug = "invalid-game",
            title = "",
            releaseDate = null,
            imageUrl = null,
            rating = -1f,
            description = null,
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = emptyList(),
            platforms = emptyList(),
            developers = emptyList(),
            publishers = emptyList(),
            tags = emptyList(),
            screenshots = emptyList(),
            stores = emptyList(),
            playtime = null,
            movies = emptyList()
        )

        // When
        val result = repository.addFavorite(invalidGame)

        // Then
        // Der Test sollte nicht abstürzen, auch wenn es zu einem Fehler kommt
        assertTrue(result is Resource.Success || result is Resource.Error)
    }
} 