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
class GameDetailCacheDaoTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: GameDetailCacheDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GameDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.gameDetailCacheDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    private fun createDetailEntity(
        id: Int,
        title: String = "Test Game $id",
    ): GameDetailCacheEntity =
        GameDetailCacheEntity(
            id = id,
            slug = "test-game-$id",
            title = title,
            releaseDate = "2023-01-01",
            imageUrl = "https://example.com/image$id.jpg",
            rating = 4.5f,
            description = "Beschreibung $id",
            metacritic = 80,
            website = "https://example.com/game$id",
            esrbRating = "USK 12",
            genres = "[\"Action\"]",
            platforms = "[\"PC\"]",
            developers = "[\"Dev\"]",
            publishers = "[\"Pub\"]",
            tags = "[\"Tag\"]",
            screenshots = "[\"https://example.com/screenshot$id.jpg\"]",
            stores = "[\"Steam\"]",
            playtime = 10,
            movies = "[]",
            detailCachedAt = System.currentTimeMillis()
        )

    @Test
    fun insertAndGetGameDetail() = runTest {
        val entity = createDetailEntity(1)
        dao.insertGameDetail(entity)
        val loaded = dao.getGameDetailById(1)
        assertNotNull(loaded)
        assertEquals("Test Game 1", loaded?.title)
    }

    @Test
    fun updateGameDetail() = runTest {
        val entity = createDetailEntity(1)
        dao.insertGameDetail(entity)
        val updated = entity.copy(title = "Updated Title")
        dao.insertGameDetail(updated)
        val loaded = dao.getGameDetailById(1)
        assertNotNull(loaded)
        assertEquals("Updated Title", loaded?.title)
    }

    @Test
    fun removeGameDetail() = runTest {
        val entity = createDetailEntity(1)
        dao.insertGameDetail(entity)
        dao.removeGameDetail(1)
        val loaded = dao.getGameDetailById(1)
        assertNull(loaded)
    }

    @Test
    fun clearAllGameDetails() = runTest {
        dao.insertGameDetail(createDetailEntity(1))
        dao.insertGameDetail(createDetailEntity(2))
        val countBefore = dao.getDetailCacheSize()
        dao.clearAllGameDetails()
        val countAfter = dao.getDetailCacheSize()
        assertEquals(2, countBefore)
        assertEquals(0, countAfter)
    }

    @Test
    fun getDetailCacheSize() = runTest {
        assertEquals(0, dao.getDetailCacheSize())
        dao.insertGameDetail(createDetailEntity(1))
        assertEquals(1, dao.getDetailCacheSize())
        dao.insertGameDetail(createDetailEntity(2))
        assertEquals(2, dao.getDetailCacheSize())
    }

    @Test
    fun isGameDetailCached() = runTest {
        assertFalse(dao.isGameDetailCached(1))
        dao.insertGameDetail(createDetailEntity(1))
        assertTrue(dao.isGameDetailCached(1))
    }

    @Test
    fun getAllGameDetails() = runTest {
        val entity1 = createDetailEntity(1)
        val entity2 = createDetailEntity(2)
        dao.insertGameDetail(entity1)
        dao.insertGameDetail(entity2)
        val all = dao.getAllGameDetails().first()
        assertEquals(2, all.size)
        assertTrue(all.any { it.id == 1 })
        assertTrue(all.any { it.id == 2 })
    }
} 