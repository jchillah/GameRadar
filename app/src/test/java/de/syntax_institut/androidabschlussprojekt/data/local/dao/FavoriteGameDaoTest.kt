package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.syntax_institut.androidabschlussprojekt.data.local.GameDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.entities.FavoriteGameEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class FavoriteGameDaoTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: FavoriteGameDao

    private val testGame1 = FavoriteGameEntity(
        id = 1,
        title = "Test Game 1",
        releaseDate = "2023-01-01",
        imageUrl = "https://example.com/image1.jpg",
        rating = 4.5f,
        description = "Test description 1",
        metacritic = 85,
        website = "https://example.com/game1",
        esrbRating = "T",
        genres = "[\"Action\", \"Adventure\"]",
        platforms = "[\"PC\", \"PlayStation 5\"]",
        developers = "[\"Developer 1\"]",
        publishers = "[\"Publisher 1\"]",
        tags = "[\"Tag1\", \"Tag2\"]",
        screenshots = "[\"https://example.com/screenshot1.jpg\"]",
        stores = "[\"Steam\", \"PlayStation Store\"]",
        playtime = 20,
        addedAt = 1000L
    )

    private val testGame2 = FavoriteGameEntity(
        id = 2,
        title = "Test Game 2",
        releaseDate = "2023-01-02",
        imageUrl = "https://example.com/image2.jpg",
        rating = 4.0f,
        description = "Test description 2",
        metacritic = 80,
        website = "https://example.com/game2",
        esrbRating = "E",
        genres = "[\"RPG\", \"Strategy\"]",
        platforms = "[\"PC\", \"Xbox Series S/X\"]",
        developers = "[\"Developer 2\"]",
        publishers = "[\"Publisher 2\"]",
        tags = "[\"Tag3\", \"Tag4\"]",
        screenshots = "[\"https://example.com/screenshot2.jpg\"]",
        stores = "[\"Steam\", \"Xbox Store\"]",
        playtime = 30,
        addedAt = 2000L
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GameDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteGameDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and get favorite by id`() = runTest {
        // When
        dao.insertFavorite(testGame1)
        val result = dao.getFavoriteById(1)

        // Then
        assertNotNull(result)
        assertEquals(testGame1.id, result.id)
        assertEquals(testGame1.title, result.title)
        assertEquals(testGame1.rating, result.rating)
    }

    @Test
    fun `get all favorites returns empty list initially`() = runTest {
        // When
        val result = dao.getAllFavorites().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `get all favorites returns all inserted favorites`() = runTest {
        // Given
        dao.insertFavorite(testGame1)
        dao.insertFavorite(testGame2)

        // When
        val result = dao.getAllFavorites().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(testGame2.id, result[0].id) // Neueste zuerst (addedAt DESC)
        assertEquals(testGame1.id, result[1].id)
    }

    @Test
    fun `isFavorite returns false for non-existent game`() = runTest {
        // When
        val result = dao.isFavorite(999)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isFavorite returns true for existing favorite`() = runTest {
        // Given
        dao.insertFavorite(testGame1)

        // When
        val result = dao.isFavorite(testGame1.id)

        // Then
        assertTrue(result)
    }

    @Test
    fun `remove favorite removes the game`() = runTest {
        // Given
        dao.insertFavorite(testGame1)
        assertTrue(dao.isFavorite(testGame1.id))

        // When
        dao.removeFavorite(testGame1.id)

        // Then
        assertFalse(dao.isFavorite(testGame1.id))
        assertNull(dao.getFavoriteById(testGame1.id))
    }

    @Test
    fun `clear all favorites removes all games`() = runTest {
        // Given
        dao.insertFavorite(testGame1)
        dao.insertFavorite(testGame2)
        assertEquals(2, dao.getFavoriteCount())

        // When
        dao.clearAllFavorites()

        // Then
        assertEquals(0, dao.getFavoriteCount())
        assertTrue(dao.getAllFavorites().first().isEmpty())
    }

    @Test
    fun `get favorite count returns correct number`() = runTest {
        // Given
        assertEquals(0, dao.getFavoriteCount())

        // When
        dao.insertFavorite(testGame1)
        dao.insertFavorite(testGame2)

        // Then
        assertEquals(2, dao.getFavoriteCount())
    }

    @Test
    fun `search favorites returns matching games`() = runTest {
        // Given
        dao.insertFavorite(testGame1)
        dao.insertFavorite(testGame2)

        // When
        val result = dao.searchFavorites("Game 1").first()

        // Then
        assertEquals(1, result.size)
        assertEquals(testGame1.id, result[0].id)
    }

    @Test
    fun `search favorites returns empty list for no matches`() = runTest {
        // Given
        dao.insertFavorite(testGame1)

        // When
        val result = dao.searchFavorites("NonExistent").first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `insert with same id updates existing favorite`() = runTest {
        // Given
        dao.insertFavorite(testGame1)
        val updatedGame = testGame1.copy(title = "Updated Title", rating = 5.0f)

        // When
        dao.insertFavorite(updatedGame)
        val result = dao.getFavoriteById(testGame1.id)

        // Then
        assertNotNull(result)
        assertEquals("Updated Title", result.title)
        assertEquals(5.0f, result.rating)
    }
} 