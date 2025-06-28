package de.syntax_institut.androidabschlussprojekt.data.repositories

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.syntax_institut.androidabschlussprojekt.data.local.GameDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.dao.FavoriteGameDao
import de.syntax_institut.androidabschlussprojekt.data.local.entities.FavoriteGameEntity
import de.syntax_institut.androidabschlussprojekt.data.local.models.Game
import de.syntax_institut.androidabschlussprojekt.utils.Resource
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
class FavoritesRepositoryTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: FavoriteGameDao
    private lateinit var repository: FavoritesRepository

    private val testGame = Game(
        id = 1,
        title = "Test Game",
        releaseDate = "2023-01-01",
        imageUrl = "https://example.com/image.jpg",
        rating = 4.5f,
        description = "Test description",
        metacritic = 85,
        website = "https://example.com/game",
        esrbRating = "T",
        genres = listOf("Action", "Adventure"),
        platforms = listOf("PC", "PlayStation 5"),
        developers = listOf("Developer 1"),
        publishers = listOf("Publisher 1"),
        tags = listOf("Tag1", "Tag2"),
        screenshots = listOf("https://example.com/screenshot.jpg"),
        stores = listOf("Steam", "PlayStation Store"),
        playtime = 20
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GameDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteGameDao()
        repository = FavoritesRepository(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getAllFavorites returns empty list initially`() = runTest {
        // When
        val result = repository.getAllFavorites().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllFavorites returns all favorites after adding games`() = runTest {
        // Given
        repository.addFavorite(testGame)
        val testGame2 = testGame.copy(id = 2, title = "Test Game 2")
        repository.addFavorite(testGame2)

        // When
        val result = repository.getAllFavorites().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(2, result[0].id) // Neueste zuerst
        assertEquals(1, result[1].id)
    }

    @Test
    fun `isFavorite returns false for non-existent game`() = runTest {
        // When
        val result = repository.isFavorite(999)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isFavorite returns true for existing favorite`() = runTest {
        // Given
        repository.addFavorite(testGame)

        // When
        val result = repository.isFavorite(testGame.id)

        // Then
        assertTrue(result)
    }

    @Test
    fun `getFavoriteById returns null for non-existent game`() = runTest {
        // When
        val result = repository.getFavoriteById(999)

        // Then
        assertNull(result)
    }

    @Test
    fun `getFavoriteById returns game for existing favorite`() = runTest {
        // Given
        repository.addFavorite(testGame)

        // When
        val result = repository.getFavoriteById(testGame.id)

        // Then
        assertNotNull(result)
        assertEquals(testGame.id, result.id)
        assertEquals(testGame.title, result.title)
        assertEquals(testGame.rating, result.rating)
    }

    @Test
    fun `addFavorite successfully adds game`() = runTest {
        // When
        val result = repository.addFavorite(testGame)

        // Then
        assertTrue(result is Resource.Success)
        assertTrue(repository.isFavorite(testGame.id))
    }

    @Test
    fun `removeFavorite successfully removes game`() = runTest {
        // Given
        repository.addFavorite(testGame)
        assertTrue(repository.isFavorite(testGame.id))

        // When
        val result = repository.removeFavorite(testGame.id)

        // Then
        assertTrue(result is Resource.Success)
        assertFalse(repository.isFavorite(testGame.id))
    }

    @Test
    fun `toggleFavorite adds game when not favorite`() = runTest {
        // Given
        assertFalse(repository.isFavorite(testGame.id))

        // When
        val result = repository.toggleFavorite(testGame)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)
        assertTrue(repository.isFavorite(testGame.id))
    }

    @Test
    fun `toggleFavorite removes game when already favorite`() = runTest {
        // Given
        repository.addFavorite(testGame)
        assertTrue(repository.isFavorite(testGame.id))

        // When
        val result = repository.toggleFavorite(testGame)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(false, (result as Resource.Success).data)
        assertFalse(repository.isFavorite(testGame.id))
    }

    @Test
    fun `clearAllFavorites removes all games`() = runTest {
        // Given
        repository.addFavorite(testGame)
        val testGame2 = testGame.copy(id = 2, title = "Test Game 2")
        repository.addFavorite(testGame2)
        assertEquals(2, repository.getFavoriteCount())

        // When
        val result = repository.clearAllFavorites()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(0, repository.getFavoriteCount())
        assertTrue(repository.getAllFavorites().first().isEmpty())
    }

    @Test
    fun `getFavoriteCount returns correct number`() = runTest {
        // Given
        assertEquals(0, repository.getFavoriteCount())

        // When
        repository.addFavorite(testGame)
        repository.addFavorite(testGame.copy(id = 2, title = "Test Game 2"))

        // Then
        assertEquals(2, repository.getFavoriteCount())
    }

    @Test
    fun `searchFavorites returns matching games`() = runTest {
        // Given
        repository.addFavorite(testGame)
        val testGame2 = testGame.copy(id = 2, title = "Another Game")
        repository.addFavorite(testGame2)

        // When
        val result = repository.searchFavorites("Test").first()

        // Then
        assertEquals(1, result.size)
        assertEquals(testGame.id, result[0].id)
    }

    @Test
    fun `searchFavorites returns empty list for no matches`() = runTest {
        // Given
        repository.addFavorite(testGame)

        // When
        val result = repository.searchFavorites("NonExistent").first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addFavorite handles database errors gracefully`() = runTest {
        // Given - Simulate database error by closing database
        database.close()

        // When
        val result = repository.addFavorite(testGame)

        // Then
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.contains("Fehler beim Hinzufügen"))
    }

    @Test
    fun `removeFavorite handles database errors gracefully`() = runTest {
        // Given
        repository.addFavorite(testGame)
        database.close()

        // When
        val result = repository.removeFavorite(testGame.id)

        // Then
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.contains("Fehler beim Entfernen"))
    }

    @Test
    fun `toggleFavorite handles database errors gracefully`() = runTest {
        // Given
        database.close()

        // When
        val result = repository.toggleFavorite(testGame)

        // Then
        assertTrue(result is Resource.Error)
        assertTrue((result as Resource.Error).message.contains("Fehler beim Umschalten"))
    }
} 