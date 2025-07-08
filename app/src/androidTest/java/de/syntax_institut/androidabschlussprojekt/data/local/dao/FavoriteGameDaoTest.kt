package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import androidx.test.core.app.*
import androidx.test.ext.junit.runners.*
import de.syntax_institut.androidabschlussprojekt.data.local.*
import de.syntax_institut.androidabschlussprojekt.data.local.entities.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class FavoriteGameDaoTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: FavoriteGameDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GameDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteGameDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndGetFavorite() = runTest {
        // Given
        val favorite = FavoriteGameEntity(
            id = 1,
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = 85,
            website = "https://example.com",
            esrbRating = "T",
            genres = "[\"Action\", \"Adventure\"]",
            platforms = "[\"PC\", \"PlayStation 5\"]",
            developers = "[\"Test Developer\"]",
            publishers = "[\"Test Publisher\"]",
            tags = "[\"Action\", \"RPG\"]",
            screenshots = "[\"https://example.com/screenshot1.jpg\"]",
            stores = "[\"Steam\", \"PlayStation Store\"]",
            playtime = 20,
            slug = "test-game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(favorite)
        val retrieved = dao.getFavoriteById(1)

        // Then
        assertNotNull(retrieved)
        assertEquals("Test Game", retrieved?.title)
        assertEquals(4.5f, retrieved?.rating ?: 0f, 0.01f)
    }

    @Test
    fun getAllFavorites() = runTest {
        // Given
        val favorite1 = FavoriteGameEntity(
            id = 1,
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = 80,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 1\"]",
            publishers = "[\"Pub 1\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        val favorite2 = FavoriteGameEntity(
            id = 2,
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = 85,
            website = null,
            esrbRating = null,
            genres = "[\"RPG\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 2\"]",
            publishers = "[\"Pub 2\"]",
            tags = "[\"RPG\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-2",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(favorite1)
        dao.insertFavorite(favorite2)
        val allFavorites = dao.getAllFavorites().first()

        // Then
        assertEquals(2, allFavorites.size)
        assertEquals("Game 2", allFavorites[0].title) // Neueste zuerst (addedAt DESC)
        assertEquals("Game 1", allFavorites[1].title)
    }

    @Test
    fun isFavorite() = runTest {
        // Given
        val favorite = FavoriteGameEntity(
            id = 1,
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev\"]",
            publishers = "[\"Pub\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "test-game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        val isFavoriteBefore = dao.isFavorite(1)
        dao.insertFavorite(favorite)
        val isFavoriteAfter = dao.isFavorite(1)

        // Then
        assertFalse(isFavoriteBefore)
        assertTrue(isFavoriteAfter)
    }

    @Test
    fun removeFavorite() = runTest {
        // Given
        val favorite = FavoriteGameEntity(
            id = 1,
            title = "Test Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image.jpg",
            rating = 4.5f,
            description = "Test description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev\"]",
            publishers = "[\"Pub\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "test-game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(favorite)
        val isFavoriteBefore = dao.isFavorite(1)
        dao.removeFavorite(1)
        val isFavoriteAfter = dao.isFavorite(1)

        // Then
        assertTrue(isFavoriteBefore)
        assertFalse(isFavoriteAfter)
    }

    @Test
    fun clearAllFavorites() = runTest {
        // Given
        val favorite1 = FavoriteGameEntity(
            id = 1,
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 1\"]",
            publishers = "[\"Pub 1\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        val favorite2 = FavoriteGameEntity(
            id = 2,
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"RPG\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 2\"]",
            publishers = "[\"Pub 2\"]",
            tags = "[\"RPG\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-2",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(favorite1)
        dao.insertFavorite(favorite2)
        val countBefore = dao.getFavoriteCount()
        dao.clearAllFavorites()
        val countAfter = dao.getFavoriteCount()

        // Then
        assertEquals(2, countBefore)
        assertEquals(0, countAfter)
    }

    @Test
    fun getFavoriteCount() = runTest {
        // Given
        val favorite1 = FavoriteGameEntity(
            id = 1,
            title = "Game 1",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Description 1",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 1\"]",
            publishers = "[\"Pub 1\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-1",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        val favorite2 = FavoriteGameEntity(
            id = 2,
            title = "Game 2",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "Description 2",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"RPG\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 2\"]",
            publishers = "[\"Pub 2\"]",
            tags = "[\"RPG\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "game-2",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        val countBefore = dao.getFavoriteCount()
        dao.insertFavorite(favorite1)
        val countAfter1 = dao.getFavoriteCount()
        dao.insertFavorite(favorite2)
        val countAfter2 = dao.getFavoriteCount()

        // Then
        assertEquals(0, countBefore)
        assertEquals(1, countAfter1)
        assertEquals(2, countAfter2)
    }

    @Test
    fun searchFavorites() = runTest {
        // Given
        val favorite1 = FavoriteGameEntity(
            id = 1,
            title = "Action Game",
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image1.jpg",
            rating = 4.0f,
            description = "Action game description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 1\"]",
            publishers = "[\"Pub 1\"]",
            tags = "[\"Action\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "action-game",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        val favorite2 = FavoriteGameEntity(
            id = 2,
            title = "RPG Game",
            releaseDate = "2023-01-02",
            imageUrl = "https://example.com/image2.jpg",
            rating = 4.5f,
            description = "RPG game description",
            metacritic = null,
            website = null,
            esrbRating = null,
            genres = "[\"RPG\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev 2\"]",
            publishers = "[\"Pub 2\"]",
            tags = "[\"RPG\"]",
            screenshots = "[]",
            stores = "[\"Steam\"]",
            playtime = null,
            slug = "rpg-game",
            movies = "[]",
            addedAt = System.currentTimeMillis()
        )

        // When
        dao.insertFavorite(favorite1)
        dao.insertFavorite(favorite2)
        
        val actionResults = dao.searchFavorites("Action").first()
        val rpgResults = dao.searchFavorites("RPG").first()
        val nonExistentResults = dao.searchFavorites("NonExistent").first()

        // Then
        assertEquals(1, actionResults.size)
        assertEquals("Action Game", actionResults[0].title)
        
        assertEquals(1, rpgResults.size)
        assertEquals("RPG Game", rpgResults[0].title)
        
        assertEquals(0, nonExistentResults.size)
    }
} 