package de.syntax_institut.androidabschlussprojekt.utils

import de.syntax_institut.androidabschlussprojekt.data.local.dao.*
import de.syntax_institut.androidabschlussprojekt.data.local.models.*

/**
 * Utility für Cache-Statistiken und Optimierung.
 */
object CacheUtils {
    /**
     * Optimiert den Spiele-Cache, indem alte Einträge entfernt werden.
     * Entfernt Einträge, die älter als 7 Tage sind.
     */
    suspend fun optimizeCache(gameCacheDao: GameCacheDao) {
        try {
            val currentSize = gameCacheDao.getCacheSize()
            val oldestCacheTime = gameCacheDao.getOldestCacheTime()
            if (oldestCacheTime != null) {
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                gameCacheDao.deleteExpiredGames(sevenDaysAgo)
                val newSize = gameCacheDao.getCacheSize()
                AppLogger.d("CacheUtils", "Cache optimiert: $currentSize -> $newSize Einträge")
            }
        } catch (e: Exception) {
            AppLogger.e("CacheUtils", "Fehler bei Cache-Optimierung", e)
        }
    }

    /**
     * Liefert Statistiken zum Spiele-Cache.
     */
    suspend fun getCacheStats(gameCacheDao: GameCacheDao): CacheStats {
        val totalSize = gameCacheDao.getCacheSize()
        // val oldestTime = gameCacheDao.getOldestCacheTime() // entfällt
        // val isExpired = oldestTime?.let { !NetworkUtils.isCacheValid(it) } != false // entfällt
        return CacheStats(
            count = totalSize,
            size = 0L // Platzhalter: Hier könnte die tatsächliche Cache-Größe in Bytes stehen, falls verfügbar
        )
    }
} 